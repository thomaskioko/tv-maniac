package com.thomaskioko.tvmaniac.domain.followedshows

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor.Param
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class FollowedShowsSyncInteractor(
    private val followedShowsRepository: FollowedShowsRepository,
    private val showContentSyncInteractor: ShowContentSyncInteractor,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<Unit>() {

    override suspend fun doWork(params: Unit) {
        withContext(dispatchers.io) {
            followedShowsRepository.syncFollowedShows()

            val followedShows = followedShowsRepository.observeFollowedShows().first()
            logger.debug(TAG, "Syncing content for ${followedShows.size} followed shows.")

            followedShows.forEach { show ->
                showContentSyncInteractor.executeSync(
                    Param(showId = show.tmdbId, isUserInitiated = false),
                )
            }

            logger.debug(TAG, "Followed shows content sync complete")
        }
    }

    private companion object {
        private const val TAG = "FollowedShowsSyncInteractor"
    }
}
