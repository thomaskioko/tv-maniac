package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.ApiRateLimiter
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.withRateLimitTracking
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

private const val SEASON_SYNC_CONCURRENCY = 2

@Inject
public class ShowContentSyncInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val datastoreRepository: DatastoreRepository,
    private val apiRateLimiter: ApiRateLimiter,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<ShowContentSyncInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            try {
                showDetailsRepository.fetchShowDetails(
                    id = params.traktId,
                    forceRefresh = params.forceRefresh,
                )

                fetchShowSeasonDetails(
                    showTraktId = params.traktId,
                    forceRefresh = params.forceRefresh,
                )

                watchedEpisodeSyncRepository.syncShowEpisodeWatches(
                    showTraktId = params.traktId,
                    forceRefresh = params.forceRefresh,
                )
            } catch (t: Throwable) {
                logger.error("Error while updating show seasons/episodes: ${params.traktId}", t)
            }
        }
    }

    private suspend fun fetchShowSeasonDetails(
        showTraktId: Long,
        forceRefresh: Boolean,
    ) {
        val includeSpecials = datastoreRepository.getIncludeSpecials()
        val seasons = seasonsRepository.getSeasonsByShowId(showTraktId, includeSpecials)

        seasons.parallelForEach(concurrency = SEASON_SYNC_CONCURRENCY) { season ->
            apiRateLimiter.withRateLimitTracking {
                seasonDetailsRepository.fetchSeasonDetails(
                    param = SeasonDetailsParam(
                        showTraktId = showTraktId,
                        seasonId = season.season_id.id,
                        seasonNumber = season.season_number,
                    ),
                    forceRefresh = forceRefresh,
                )
            }
        }
    }

    public data class Param(
        val traktId: Long,
        val forceRefresh: Boolean = false,
        val isUserInitiated: Boolean = false,
    )
}
