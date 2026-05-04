package com.thomaskioko.tvmaniac.upnext.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPNEXT_FULL_SYNC
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.upnext.api.UpNextDao
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUpNextRepository(
    private val upNextDao: UpNextDao,
    private val showUpNextStore: ShowUpNextStore,
    private val datastoreRepository: DatastoreRepository,
    private val episodesDao: EpisodesDao,
    private val followedShowsDao: FollowedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val requestManagerRepository: RequestManagerRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
) : UpNextRepository {

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        upNextDao.observeNextEpisodesFromCache()

    override fun observeFollowedShowsCount(): Flow<Int> =
        followedShowsDao.entriesObservable()
            .map { entries -> entries.count { it.pendingAction != PendingAction.DELETE } }

    override suspend fun fetchUpNextEpisodes(forceRefresh: Boolean) {
        if (!forceRefresh && isSyncValid()) return

        val followedShows = followedShowsDao.entriesExcludingDeleted()
        if (followedShows.isEmpty()) {
            logger.debug(TAG, "No followed shows found, skipping UpNext refresh")
            return
        }

        logger.debug(
            TAG,
            "Refreshing UpNext for ${followedShows.size} followed shows (forceRefresh=$forceRefresh)",
        )

        val includeSpecials = datastoreRepository.getIncludeSpecials()

        followedShows.forEach { show ->
            ensureShowExists(show.traktId)
            fetchShowUpNext(show.traktId, forceRefresh)

            seasonDetailsRepository.syncShowSeasonDetails(
                showTraktId = show.traktId,
                forceRefresh = forceRefresh,
            )

            populateUpNextIfMissing(show.traktId, includeSpecials)
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
        ensureShowExists(showTraktId)
        fetchShowUpNext(showTraktId, forceRefresh)
        populateUpNextIfMissing(showTraktId)
    }

    override suspend fun fetchUpNext(
        showTraktId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        try {
            ensureShowExists(showTraktId)
            showUpNextStore.fresh(showTraktId) { logger.debug(TAG, it) }
            populateUpNextFromLocal(showTraktId, lastWatchedAt = dateTimeProvider.nowMillis())
        } catch (e: Exception) {
            logger.error(TAG, "Remote UpNext refresh failed, advancing locally: ${e.message}")
            upNextDao.advanceAfterWatched(
                showTraktId = showTraktId,
                watchedSeason = seasonNumber,
                watchedEpisode = episodeNumber,
            )
            throw e
        }
    }

    private suspend fun fetchShowUpNext(showTraktId: Long, forceRefresh: Boolean) {
        when {
            forceRefresh -> showUpNextStore.fresh(showTraktId) { logger.debug(TAG, it) }
            else -> showUpNextStore.get(showTraktId) { logger.debug(TAG, it) }
        }
    }

    private suspend fun isSyncValid(): Boolean {
        val hasCachedData = upNextDao.hasAnyEpisodes()
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
        val tvShowExists = tvShowsDao.existsByTraktId(showTraktId)
        val seasonsLoaded = seasonsRepository.getSeasonsByShowId(showTraktId, includeSpecials = true).isNotEmpty()
        if (!tvShowExists || !seasonsLoaded) {
            logger.debug(
                TAG,
                "Show $showTraktId graph incomplete (tvShow=$tvShowExists, seasons=$seasonsLoaded), fetching details",
            )
            showDetailsRepository.fetchShowDetails(
                id = showTraktId,
                forceRefresh = true,
            )
        }
    }

    private suspend fun populateUpNextIfMissing(showTraktId: Long) {
        if (upNextDao.existsForShow(showTraktId)) return
        populateUpNextFromLocal(showTraktId)
    }

    private suspend fun populateUpNextIfMissing(showTraktId: Long, includeSpecials: Boolean) {
        if (upNextDao.existsForShow(showTraktId)) return
        populateUpNextFromLocal(showTraktId, includeSpecials)
    }

    private suspend fun populateUpNextFromLocal(
        showTraktId: Long,
        lastWatchedAt: Long? = null,
    ) {
        populateUpNextFromLocal(showTraktId, datastoreRepository.getIncludeSpecials(), lastWatchedAt)
    }

    private suspend fun populateUpNextFromLocal(
        showTraktId: Long,
        includeSpecials: Boolean,
        lastWatchedAt: Long? = null,
    ) {
        val nextEpisode = episodesDao.getNextEpisodeForShow(showTraktId, includeSpecials)
        if (nextEpisode != null) {
            upNextDao.upsert(
                showTraktId = showTraktId,
                episodeTraktId = nextEpisode.episode_id.id,
                seasonNumber = nextEpisode.season_number,
                episodeNumber = nextEpisode.episode_number,
                title = nextEpisode.episode_name,
                overview = nextEpisode.overview,
                runtime = nextEpisode.runtime,
                firstAired = nextEpisode.first_aired,
                imageUrl = nextEpisode.still_path,
                isShowComplete = false,
                lastEpisodeSeason = null,
                lastEpisodeNumber = null,
                traktLastWatchedAt = lastWatchedAt,
                updatedAt = dateTimeProvider.nowMillis(),
            )
            logger.debug(TAG, "Populated local up next for show $showTraktId: S${nextEpisode.season_number}E${nextEpisode.episode_number}")
        } else {
            logger.debug(TAG, "No next episode found locally for show $showTraktId")
        }
    }

    private companion object {
        private const val TAG = "DefaultUpNextRepository"
    }
}
