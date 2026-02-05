package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class SyncLibraryInteractor(
    private val libraryRepository: LibraryRepository,
    private val followedShowsRepository: FollowedShowsRepository,
    private val showDetailsRepository: ShowDetailsRepository,
    private val watchProviderRepository: WatchProviderRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<SyncLibraryInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            traktActivityRepository.fetchLatestActivities(params.forceRefresh)

            val needsSync = params.forceRefresh || libraryRepository.needsSync()

            if (needsSync) {
                logger.debug(TAG, "Syncing library watchlist")
                libraryRepository.syncLibrary(params.forceRefresh)
            }

            val followedShows = followedShowsRepository.getFollowedShows()
            logger.debug(TAG, "Syncing ${followedShows.size} followed shows")

            followedShows.parallelForEach(concurrency = LIBRARY_SYNC_CONCURRENCY) { show ->
                ensureActive()

                showDetailsRepository.fetchShowDetails(
                    id = show.traktId,
                    forceRefresh = params.forceRefresh,
                )

                ensureActive()

                watchProviderRepository.fetchWatchProviders(
                    traktId = show.traktId,
                    forceRefresh = params.forceRefresh,
                )
            }

            logger.debug(TAG, "Library sync complete")
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "SyncLibraryInteractor"
        private const val LIBRARY_SYNC_CONCURRENCY = 2
    }
}
