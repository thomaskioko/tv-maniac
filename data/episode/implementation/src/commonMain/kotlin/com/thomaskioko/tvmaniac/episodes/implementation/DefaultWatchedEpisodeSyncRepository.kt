package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant.Companion.fromEpochMilliseconds

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchedEpisodeSyncRepository(
    private val dao: WatchedEpisodeDao,
    private val episodesDao: EpisodesDao,
    private val seasonsDao: SeasonsDao,
    private val followedShowsDao: FollowedShowsDao,
    private val followedShowsRepository: FollowedShowsRepository,
    private val dataSource: EpisodeWatchesDataSource,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val datastoreRepository: DatastoreRepository,
    private val lastRequestStore: EpisodeWatchesLastRequestStore,
    private val traktAuthRepository: TraktAuthRepository,
    private val logger: Logger,
) : WatchedEpisodeSyncRepository {

    override suspend fun syncShowEpisodeWatches(showId: Long, forceRefresh: Boolean) {
        if (!isLoggedIn()) return

        followedShowsRepository.addFollowedShow(showId)

        logger.debug(TAG, "Syncing episode watches for show $showId")

        processPendingUploads()
        processPendingDeletes()

        if (forceRefresh || lastRequestStore.isShowRequestExpired(showId)) {
            val entry = followedShowsDao.entryWithTmdbId(showId)
            val traktId = entry?.traktId ?: return

            syncShowWatches(showId, traktId)
            lastRequestStore.updateShowLastRequest(showId)
            logger.debug(TAG, "Show $showId sync completed (pulled remote)")
        } else {
            logger.debug(TAG, "Show $showId sync skipped (cache valid)")
        }
    }

    override fun observePendingSyncCount(): Flow<Long> = dao.observePendingSyncCount()

    private suspend fun isLoggedIn(): Boolean {
        return traktAuthRepository.state.first() == TraktAuthState.LOGGED_IN
    }

    private suspend fun processPendingUploads() {
        val pending = dao.entriesWithUploadPendingAction()

        if (pending.isEmpty()) return

        logger.debug(TAG, "Processing ${pending.size} pending uploads")

        val entries = pending.map { episode ->
            WatchedEpisodeEntry(
                id = episode.id,
                showId = episode.show_id.id,
                episodeId = episode.episode_id?.id,
                seasonNumber = episode.season_number,
                episodeNumber = episode.episode_number,
                watchedAt = fromEpochMilliseconds(episode.watched_at),
                traktId = episode.trakt_id,
                pendingAction = PendingAction.UPLOAD,
            )
        }

        dataSource.addEpisodeWatches(entries)

        pending.forEach { episode ->
            dao.updatePendingAction(episode.id, PendingAction.NOTHING)
        }

        logger.debug(TAG, "Successfully uploaded ${pending.size} episodes")
    }

    private suspend fun processPendingDeletes() {
        val pending = dao.entriesWithDeletePendingAction()

        if (pending.isEmpty()) return

        logger.debug(TAG, "Processing ${pending.size} pending deletes")

        val traktIds = pending.mapNotNull { it.trakt_id }
        if (traktIds.isNotEmpty()) {
            dataSource.removeEpisodeWatches(traktIds)
        }

        pending.forEach { episode ->
            dao.hardDeleteById(episode.id)
        }

        logger.debug(TAG, "Successfully deleted ${pending.size} episodes")
    }

    private suspend fun syncShowWatches(tmdbId: Long, traktShowId: Long) {
        val remoteWatches = dataSource.getShowEpisodeWatches(traktShowId)

        if (remoteWatches.isEmpty()) {
            logger.debug(TAG, "No remote watches for show $tmdbId")
            return
        }

        logger.debug(TAG, "Found ${remoteWatches.size} remote watches for show $tmdbId")

        val uniqueSeasons = remoteWatches.map { it.seasonNumber }.distinct()
        for (seasonNumber in uniqueSeasons) {
            ensureSeasonDetailsExist(tmdbId, seasonNumber)
        }

        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        remoteWatches.forEach { remoteEntry ->
            val episode = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
                showId = tmdbId,
                seasonNumber = remoteEntry.seasonNumber,
                episodeNumber = remoteEntry.episodeNumber,
            )

            dao.upsertFromTrakt(
                showId = tmdbId,
                episodeId = episode?.id?.id,
                seasonNumber = remoteEntry.seasonNumber,
                episodeNumber = remoteEntry.episodeNumber,
                watchedAt = remoteEntry.watchedAt.toEpochMilliseconds(),
                traktId = remoteEntry.traktId ?: 0L,
                syncedAt = kotlin.time.Clock.System.now().toEpochMilliseconds(),
                includeSpecials = includeSpecials,
            )
        }
    }

    private suspend fun ensureSeasonDetailsExist(showId: Long, seasonNumber: Long) {
        val season = seasonsDao.getSeasonByShowAndNumber(showId, seasonNumber) ?: return

        val hasEpisodes = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
            showId = showId,
            seasonNumber = seasonNumber,
            episodeNumber = 1,
        ) != null

        if (!hasEpisodes) {
            logger.debug(TAG, "Fetching season $seasonNumber details for show $showId")
            seasonDetailsRepository.fetchSeasonDetails(
                param = SeasonDetailsParam(
                    showId = showId,
                    seasonId = season.id.id,
                    seasonNumber = seasonNumber,
                ),
                forceRefresh = false,
            )
        }
    }

    private companion object {
        const val TAG = "WatchedEpisodeSyncRepository"
    }
}
