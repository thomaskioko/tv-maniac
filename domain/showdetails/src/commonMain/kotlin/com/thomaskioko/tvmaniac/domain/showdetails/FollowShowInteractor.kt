package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import dev.zacsweers.metro.Inject

@Inject
public class FollowShowInteractor(
    private val followedShowsRepository: FollowedShowsRepository,
    private val showContentSyncInteractor: ShowContentSyncInteractor,
    private val appScopeLauncher: AppScopeLauncher,
) : Interactor<FollowShowInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        followedShowsRepository.addFollowedShow(params.traktId)

        appScopeLauncher.launch(TAG) {
            showContentSyncInteractor.executeSync(
                ShowContentSyncInteractor.Param(
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
