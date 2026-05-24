package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import dev.zacsweers.metro.Inject

@Inject
public class FollowShowInteractor(
    private val followedShowsRepository: FollowedShowsRepository,
    private val libraryRepository: LibraryRepository,
    private val syncShowMetadataInteractor: SyncShowMetadataInteractor,
    private val appScopeLauncher: AppScopeLauncher,
) : Interactor<FollowShowInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        followedShowsRepository.addFollowedShow(params.traktId)

        appScopeLauncher.launch(TAG) {
            libraryRepository.syncPendingFollowedShows()
            syncShowMetadataInteractor.executeSync(
                SyncShowMetadataInteractor.Param(
                    traktId = params.traktId,
                    forceRefresh = params.forceRefresh,
                ),
            )
        }
    }

    public data class Param(
        val traktId: Long,
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "FollowShowInteractor"
    }
}
