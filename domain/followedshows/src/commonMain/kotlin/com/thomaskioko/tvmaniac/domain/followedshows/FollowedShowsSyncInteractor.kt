package com.thomaskioko.tvmaniac.domain.followedshows

import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.followedshows.FollowedShowsSyncInteractor.Param
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class FollowedShowsSyncInteractor(
    private val followedShowsRepository: FollowedShowsRepository,
    private val showContentSyncInteractor: ShowContentSyncInteractor,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            followedShowsRepository.syncFollowedShows(params.forceRefresh)

            val followedShows = followedShowsRepository.observeFollowedShows().first()
            logger.debug(TAG, "Syncing content for ${followedShows.size} followed shows.")

            followedShows.parallelForEach { show ->
                currentCoroutineContext().ensureActive()
                showContentSyncInteractor.executeSync(
                    ShowContentSyncInteractor.Param(
                        traktId = show.traktId,
                        forceRefresh = params.forceRefresh,
                        isUserInitiated = false,
                    ),
                )
            }

            logger.debug(TAG, "Followed shows content sync complete")
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "FollowedShowsSyncInteractor"
    }
}
