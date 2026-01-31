package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Episode
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEASONS_EPISODES_SYNC
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class SeasonsWithEpisodesStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val seasonsDao: SeasonsDao,
    private val episodesDao: EpisodesDao,
    private val dateTimeProvider: DateTimeProvider,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowSeasons>> by storeBuilder(
    fetcher = Fetcher.of { showTraktId: Long ->
        traktRemoteDataSource.getSeasonsWithEpisodes(showTraktId).getOrThrow()
    },
    sourceOfTruth = SourceOfTruth.of<Long, List<TraktSeasonEpisodesResponse>, List<ShowSeasons>>(
        reader = { showTraktId ->
            seasonsDao.observeSeasonsByShowTraktId(showTraktId)
        },
        writer = { showTraktId, seasons ->
            databaseTransactionRunner {
                seasons.forEach { seasonResponse ->
                    val seasonId = seasonResponse.ids.trakt.toLong()

                    seasonsDao.upsert(
                        Season(
                            id = Id(seasonId),
                            show_trakt_id = Id(showTraktId),
                            season_number = seasonResponse.number.toLong(),
                            episode_count = seasonResponse.episodeCount.toLong(),
                            title = seasonResponse.title ?: "Season ${seasonResponse.number}",
                            overview = seasonResponse.overview,
                            image_url = null,
                        ),
                    )

                    seasonResponse.episodes.forEach { episodeResponse ->
                        episodesDao.insert(
                            Episode(
                                id = Id(episodeResponse.ids.trakt.toLong()),
                                season_id = Id(seasonId),
                                show_trakt_id = Id(showTraktId),
                                episode_number = episodeResponse.episodeNumber.toLong(),
                                title = episodeResponse.title,
                                overview = episodeResponse.overview ?: "",
                                runtime = episodeResponse.runtime?.toLong() ?: 0L,
                                vote_count = episodeResponse.votes?.toLong() ?: 0L,
                                ratings = episodeResponse.ratings ?: 0.0,
                                image_url = null,
                                trakt_id = episodeResponse.ids.trakt.toLong(),
                                first_aired = dateTimeProvider.isoDateToEpoch(episodeResponse.firstAired),
                            ),
                        )
                    }
                }

                requestManagerRepository.upsert(
                    entityId = showTraktId,
                    requestType = SEASONS_EPISODES_SYNC.name,
                )
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
                    entityId = firstSeason.show_trakt_id.id,
                    requestType = SEASONS_EPISODES_SYNC.name,
                    threshold = SEASONS_EPISODES_SYNC.duration,
                )
            }
        } ?: false
    },
).build()
