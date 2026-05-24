package com.thomaskioko.tvmaniac.domain.followedshows

import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import dev.zacsweers.metro.Inject

@Inject
public class UnfollowShowInteractor(
    private val followedShowsRepository: FollowedShowsRepository,
    private val libraryRepository: LibraryRepository,
    private val appScopeLauncher: AppScopeLauncher,
) : Interactor<Long>() {

    override suspend fun doWork(params: Long) {
        followedShowsRepository.removeFollowedShow(params)

        appScopeLauncher.launch(TAG) {
            libraryRepository.syncPendingFollowedShows()
        }
    }

    private companion object {
        private const val TAG = "UnfollowShowInteractor"
    }
}
