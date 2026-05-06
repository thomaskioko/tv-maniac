package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

@Inject
public class ShowContentSyncInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
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

                seasonDetailsRepository.syncShowSeasonDetails(
                    showTraktId = params.traktId,
                    forceRefresh = params.forceRefresh,
                )

                watchedEpisodeSyncRepository.syncShowEpisodeWatches(
                    showTraktId = params.traktId,
                    forceRefresh = params.forceRefresh,
                )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                logger.error(TAG, "Failed to sync show ${params.traktId}", throwable)
            }
        }
    }

    public data class Param(
        val traktId: Long,
        val forceRefresh: Boolean = false,
        val isUserInitiated: Boolean = false,
    )

    private companion object {
        private const val TAG = "ShowContentSync"
    }
}
