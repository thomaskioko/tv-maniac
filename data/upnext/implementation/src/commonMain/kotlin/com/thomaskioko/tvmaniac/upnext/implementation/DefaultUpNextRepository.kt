package com.thomaskioko.tvmaniac.upnext.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPNEXT_FULL_SYNC
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUpNextRepository(
    private val nextEpisodeDao: NextEpisodeDao,
    private val datastoreRepository: DatastoreRepository,
    private val followedShowsDao: FollowedShowsDao,
    private val continueWatchingDao: ContinueWatchingDao,
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val requestManagerRepository: RequestManagerRepository,
    private val logger: Logger,
) : UpNextRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        datastoreRepository.observeIncludeSpecials()
            .flatMapLatest { includeSpecials ->
                nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials)
            }

    override fun observeFollowedShowsCount(): Flow<Int> =
        followedShowsDao.entriesObservable()
            .map { entries -> entries.count { it.pendingAction != PendingAction.DELETE } }

    override suspend fun fetchUpNextEpisodes(forceRefresh: Boolean) {
        if (!forceRefresh && isSyncValid()) return

        val followedTraktIds = followedShowsDao.entriesExcludingDeleted().map { it.traktId }
        val watchedTraktIds = continueWatchingDao.entries().map { it.traktId }
        val traktIds = (followedTraktIds + watchedTraktIds).distinct()
        if (traktIds.isEmpty()) {
            logger.debug(TAG, "No followed or watched shows found, skipping UpNext refresh")
            return
        }

        logger.debug(
            TAG,
            "Refreshing UpNext metadata for ${traktIds.size} shows (forceRefresh=$forceRefresh)",
        )

        traktIds.forEach { traktId ->
            // Ensure the show graph (tvshow row + seasons) is loaded before syncing episodes;
            // `syncShowSeasonDetails` is a no-op when the local seasons table is empty, which
            // happens whenever this method races ahead of `SyncLibraryInteractor` on login.
            showDetailsRepository.fetchShowDetails(
                id = traktId,
                forceRefresh = forceRefresh,
            )
            seasonDetailsRepository.syncShowSeasonDetails(
                showTraktId = traktId,
                forceRefresh = forceRefresh,
            )
            watchedEpisodeSyncRepository.syncShowEpisodeWatches(
                showTraktId = traktId,
                forceRefresh = forceRefresh,
            )
        }

        requestManagerRepository.upsert(
            entityId = UPNEXT_FULL_SYNC.requestId,
            requestType = UPNEXT_FULL_SYNC.name,
        )

        logger.debug(TAG, "UpNext metadata refresh complete")
    }

    override suspend fun saveUpNextSortOption(sortOption: String) {
        datastoreRepository.saveUpNextSortOption(sortOption)
    }

    override fun observeUpNextSortOption(): Flow<String> =
        datastoreRepository.observeUpNextSortOption()

    private suspend fun isSyncValid(): Boolean {
        val isSyncFresh = requestManagerRepository.isRequestValid(
            requestType = UPNEXT_FULL_SYNC.name,
            threshold = UPNEXT_FULL_SYNC.duration,
        )
        if (isSyncFresh) {
            logger.debug(TAG, "UpNext full sync still valid, skipping refresh")
            return true
        }
        return false
    }

    private companion object {
        private const val TAG = "DefaultUpNextRepository"
    }
}
