package com.thomaskioko.tvmaniac.upnext.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPNEXT_FULL_SYNC
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.upnext.api.UpNextDao
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUpNextRepository(
    private val upNextDao: UpNextDao,
    private val showUpNextStore: ShowUpNextStore,
    private val datastoreRepository: DatastoreRepository,
    private val followedShowsDao: FollowedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val showDetailsRepository: ShowDetailsRepository,
    private val requestManagerRepository: RequestManagerRepository,
    private val logger: Logger,
) : UpNextRepository {

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        upNextDao.observeNextEpisodesFromCache()

    override fun observeFollowedShowsCount(): Flow<Int> =
        followedShowsDao.entriesObservable()
            .map { entries -> entries.count { it.pendingAction == PendingAction.NOTHING } }

    override suspend fun fetchUpNextEpisodes(forceRefresh: Boolean) {
        if (!forceRefresh && isSyncValid()) return

        val followedShows = followedShowsDao.entriesWithNoPendingAction()
        if (followedShows.isEmpty()) {
            logger.debug(TAG, "No followed shows found, skipping UpNext refresh")
            return
        }

        logger.debug(
            TAG,
            "Refreshing UpNext for ${followedShows.size} followed shows (forceRefresh=$forceRefresh)",
        )

        followedShows.chunked(10).forEach { batch ->
            batch.forEach { show ->
                ensureShowExists(show.traktId)

                when {
                    forceRefresh -> showUpNextStore.fresh(show.traktId)
                    else -> showUpNextStore.get(show.traktId)
                }
            }
        }

        requestManagerRepository.upsert(
            entityId = UPNEXT_FULL_SYNC.requestId,
            requestType = UPNEXT_FULL_SYNC.name,
        )

        logger.debug(TAG, "UpNext refresh complete")
    }

    override suspend fun saveUpNextSortOption(sortOption: String) {
        datastoreRepository.saveUpNextSortOption(sortOption)
    }

    override fun observeUpNextSortOption(): Flow<String> =
        datastoreRepository.observeUpNextSortOption()

    override suspend fun updateUpNextForShow(showTraktId: Long, forceRefresh: Boolean) {
        try {
            ensureShowExists(showTraktId)
        } catch (e: Exception) {
            logger.error(TAG, "Failed to ensure show $showTraktId exists: ${e.message}")
        }
        when {
            forceRefresh -> showUpNextStore.fresh(showTraktId)
            else -> showUpNextStore.get(showTraktId)
        }
    }

    override suspend fun fetchUpNext(
        showTraktId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        try {
            ensureShowExists(showTraktId)
            showUpNextStore.fresh(showTraktId)
        } catch (e: Exception) {
            logger.error(TAG, "Remote UpNext refresh failed, advancing locally: ${e.message}")
            upNextDao.advanceAfterWatched(
                showTraktId = showTraktId,
                watchedSeason = seasonNumber,
                watchedEpisode = episodeNumber,
            )
        }
    }

    private suspend fun isSyncValid(): Boolean {
        val hasCachedData = upNextDao.getNextEpisodesFromCache().isNotEmpty()
        val isSyncFresh = requestManagerRepository.isRequestValid(
            requestType = UPNEXT_FULL_SYNC.name,
            threshold = UPNEXT_FULL_SYNC.duration,
        )
        if (hasCachedData && isSyncFresh) {
            logger.debug(TAG, "UpNext full sync still valid, skipping refresh")
            return true
        }
        return false
    }

    private suspend fun ensureShowExists(showTraktId: Long) {
        if (!tvShowsDao.existsByTraktId(showTraktId)) {
            logger.debug(TAG, "Show $showTraktId not in cache, fetching details")
            showDetailsRepository.fetchShowDetails(
                id = showTraktId,
                forceRefresh = true,
            )
        }
    }

    private companion object {
        private const val TAG = "DefaultUpNextRepository"
    }
}
