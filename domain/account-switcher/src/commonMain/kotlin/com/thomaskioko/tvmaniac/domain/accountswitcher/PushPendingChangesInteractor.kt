package com.thomaskioko.tvmaniac.domain.accountswitcher

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import dev.zacsweers.metro.Inject

@Inject
public class PushPendingChangesInteractor(
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val libraryRepository: LibraryRepository,
) : Interactor<Unit>() {

    override suspend fun doWork(params: Unit) {
        watchedEpisodeSyncRepository.syncPendingEpisodes()
        libraryRepository.syncPendingFollowedShows()
    }
}
