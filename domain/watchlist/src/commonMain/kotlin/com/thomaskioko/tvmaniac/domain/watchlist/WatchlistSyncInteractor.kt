package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.extensions.DEFAULT_SYNC_CONCURRENCY
import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class WatchlistSyncInteractor(
    private val followedShowsRepository: FollowedShowsRepository,
    private val watchlistRepository: WatchlistRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val showContentSyncInteractor: ShowContentSyncInteractor,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<WatchlistSyncInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            traktActivityRepository.fetchLatestActivities(params.forceRefresh)

            watchlistRepository.syncWatchlist(params.forceRefresh)

            val followedShows = followedShowsRepository.getFollowedShows()
            logger.debug(TAG, "Syncing content for ${followedShows.size} followed shows.")

            followedShows.parallelForEach(concurrency = DEFAULT_SYNC_CONCURRENCY) { show ->
                ensureActive()

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
