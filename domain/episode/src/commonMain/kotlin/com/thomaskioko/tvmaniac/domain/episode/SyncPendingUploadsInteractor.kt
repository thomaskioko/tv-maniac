package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Inject
@SingleIn(AppScope::class)
public class SyncPendingUploadsInteractor(
    private val syncRepository: WatchedEpisodeSyncRepository,
    private val libraryRepository: LibraryRepository,
    private val listRepository: ListRepository,
    private val userRepository: UserRepository,
    private val activeProviderFeatures: () -> ProviderFeatures,
    private val accountManager: AccountManager,
    private val logger: Logger,
) : Interactor<Unit>() {

    private val pushMutex = Mutex()

    override suspend fun doWork(params: Unit) {
        if (accountManager.getActiveProvider() == null) {
            logger.debug(TAG, "User not logged in, skipping pending uploads sync")
            return
        }

        pushMutex.withLock {
            syncRepository.syncPendingEpisodes()
            libraryRepository.syncPendingFollowedShows()
            syncPendingLists()
        }
    }

    private suspend fun syncPendingLists() {
        if (!activeProviderFeatures().supportsLists) return
        val slug = userRepository.getCurrentUser()?.slug ?: return
        listRepository.syncPendingLists(slug)
    }

    private companion object {
        private const val TAG = "SyncPendingUploadsInteractor"
    }
}
