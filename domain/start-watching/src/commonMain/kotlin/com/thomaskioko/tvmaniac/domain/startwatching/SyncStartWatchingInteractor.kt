package com.thomaskioko.tvmaniac.domain.startwatching

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class SyncStartWatchingInteractor(
    private val startWatchingRepository: StartWatchingRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SyncStartWatchingInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            watchedEpisodeSyncRepository.syncAllWatchedEpisodes(params.forceRefresh)
            startWatchingRepository.syncWatchlist(params.forceRefresh)
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )
}
