package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class SyncShowEpisodeWatchesInteractor(
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SyncShowEpisodeWatchesInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            watchedEpisodeSyncRepository.syncShowEpisodeWatches(
                showId = params.showId,
                forceRefresh = params.forceRefresh,
            )
        }
    }

    public data class Param(val showId: Long, val forceRefresh: Boolean = false)
}
