package com.thomaskioko.tvmaniac.domain.startwatching

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.START_WATCHING_FIRST_SEASON_SYNC
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsEpisodesSyncRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError as NetworkSyncError

@Inject
public class SyncStartWatchingFirstSeasonInteractor(
    private val startWatchingRepository: StartWatchingRepository,
    private val seasonsEpisodesSyncRepository: SeasonsEpisodesSyncRepository,
    private val seasonsRepository: SeasonsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val requestManagerRepository: RequestManagerRepository,
    private val syncObserver: SyncObserver,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<SyncStartWatchingFirstSeasonInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            val shows = startWatchingRepository.observeStartWatching().first()
            logger.debug(TAG, "Fetching first season for ${shows.size} start-watching shows")

            for (show in shows) {
                ensureActive()

                val cacheValid = !requestManagerRepository.isRequestExpired(
                    entityId = show.traktId,
                    requestType = START_WATCHING_FIRST_SEASON_SYNC.name,
                    threshold = START_WATCHING_FIRST_SEASON_SYNC.duration,
                )
                if (!params.forceRefresh && cacheValid) continue

                val result = runCatching {
                    seasonsEpisodesSyncRepository.syncSeasonsWithEpisodes(
                        showTraktId = show.traktId,
                        forceRefresh = params.forceRefresh,
                    )

                    val firstSeason = seasonsRepository.getSeasonsByShowId(show.traktId)
                        .filter { it.season_number > 0 }
                        .minByOrNull { it.season_number }

                    if (firstSeason != null) {
                        seasonDetailsRepository.fetchSeasonDetails(
                            param = SeasonDetailsParam(
                                showTraktId = show.traktId,
                                seasonId = firstSeason.season_id.id,
                                seasonNumber = firstSeason.season_number,
                            ),
                            forceRefresh = params.forceRefresh,
                        )
                    }

                    requestManagerRepository.upsert(
                        entityId = show.traktId,
                        requestType = START_WATCHING_FIRST_SEASON_SYNC.name,
                    )
                }

                val failure = result.exceptionOrNull() ?: continue

                logger.warning(TAG, "First-season fetch failed for ${show.traktId}: ${failure.message}")
                syncObserver.log(SyncError.BackgroundSyncFailed(TAG, failure))

                if (failure.toSyncError() is NetworkSyncError.Retryable) {
                    logger.warning(TAG, "Backing off first-season fan-out after retryable failure on ${show.traktId}")
                    break
                }
            }

            logger.debug(TAG, "Start Watching first-season sync complete")
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "SyncStartWatchingFirstSeasonInteractor"
    }
}
