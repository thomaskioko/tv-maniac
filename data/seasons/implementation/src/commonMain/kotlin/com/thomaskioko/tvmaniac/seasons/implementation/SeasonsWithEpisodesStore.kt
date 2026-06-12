package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEASONS_EPISODES_SYNC
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class SeasonsWithEpisodesStore(
    private val showDetailsDataSource: TmdbShowDetailsNetworkDataSource,
    private val seasonDetailsDataSource: TmdbSeasonDetailsNetworkDataSource,
    private val seasonsDao: SeasonsDao,
    private val episodesDao: EpisodesDao,
    private val showIdResolver: ShowIdResolver,
    private val tmdbSeasonMapper: TmdbSeasonMapper,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowSeasons>> by storeBuilder(
    fetcher = Fetcher.of { tmdbShowId: Long ->
        showDetailsDataSource.getShowDetails(tmdbShowId).getOrThrow()
            .seasons
            .map { season ->
                seasonDetailsDataSource.getSeasonDetails(tmdbShowId, season.seasonNumber.toLong()).getOrThrow()
            }
    },
    sourceOfTruth = SourceOfTruth.of<Long, List<TmdbSeasonDetailsResponse>, List<ShowSeasons>>(
        reader = { showId ->
            seasonsDao.observeSeasonsByShowId(showId)
        },
        writer = { showId, responses ->
            val internalShowId = showIdResolver.showIdForTmdbId(showId)
            if (internalShowId != null) {
                databaseTransactionRunner {
                    responses.forEach { response ->
                        seasonsDao.upsert(tmdbSeasonMapper.mapToSeason(response, internalShowId))
                        episodesDao.insert(tmdbSeasonMapper.mapToEpisodes(response, internalShowId))
                    }

                    requestManagerRepository.upsert(
                        entityId = showId,
                        requestType = SEASONS_EPISODES_SYNC.name,
                    )
                }
            }
        },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by { seasonsList ->
        seasonsList.firstOrNull()?.let { firstSeason ->
            withContext(dispatchers.io) {
                !requestManagerRepository.isRequestExpired(
                    entityId = firstSeason.show_id.id,
                    requestType = SEASONS_EPISODES_SYNC.name,
                    threshold = SEASONS_EPISODES_SYNC.duration,
                )
            }
        } ?: false
    },
).build()
