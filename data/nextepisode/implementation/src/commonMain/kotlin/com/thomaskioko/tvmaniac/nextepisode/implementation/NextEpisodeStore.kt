package com.thomaskioko.tvmaniac.nextepisode.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.nextepisode.implementation.model.NextEpisodeKey
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEASON_DETAILS
import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
import com.thomaskioko.tvmaniac.util.FormatterUtil
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class NextEpisodeStore(
    private val seasonDetailsDataSource: TmdbSeasonDetailsNetworkDataSource,
    private val nextEpisodeDao: NextEpisodeDao,
    private val formatterUtil: FormatterUtil,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<NextEpisodeKey, NextEpisodeWithShow> by storeBuilder(
    fetcher = apiFetcher { key ->
        seasonDetailsDataSource.getSeasonDetails(key.showId, key.seasonNumber.toLong())
    },
    sourceOfTruth = SourceOfTruth.of<NextEpisodeKey, TmdbSeasonDetailsResponse, NextEpisodeWithShow>(
        reader = { key: NextEpisodeKey ->
            nextEpisodeDao.observeNextEpisode(key.showId)
        },
        writer = { key, response ->
            databaseTransactionRunner {
                // Extract the first episode from the fetched season as the next episode to watch
                response.episodes.firstOrNull()?.let { firstEpisode ->
                    nextEpisodeDao.upsert(
                        showId = key.showId,
                        episodeId = firstEpisode.id.toLong(),
                        episodeName = firstEpisode.name,
                        episodeNumber = firstEpisode.episodeNumber.toLong(),
                        seasonNumber = firstEpisode.seasonNumber.toLong(),
                        airDate = firstEpisode.airDate,
                        runtime = firstEpisode.runtime,
                        stillPath = firstEpisode.stillPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        },
                        overview = firstEpisode.overview,
                        isUpcoming = false,
                    )
                }

                requestManagerRepository.upsert(
                    entityId = key.showId,
                    requestType = SEASON_DETAILS.name,
                )
            }
        },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by { nextEpisodeWithShow ->
        withContext(dispatchers.io) {
            !requestManagerRepository.isRequestExpired(
                entityId = nextEpisodeWithShow?.showId ?: return@withContext false,
                requestType = SEASON_DETAILS.name,
                threshold = SEASON_DETAILS.duration,
            )
        }
    },
).build()
