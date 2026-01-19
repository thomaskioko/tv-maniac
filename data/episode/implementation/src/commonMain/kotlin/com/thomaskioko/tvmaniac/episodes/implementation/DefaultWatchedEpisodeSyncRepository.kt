package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
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
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
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

    override suspend fun syncShowEpisodeWatches(showTraktId: Long, forceRefresh: Boolean) {
        val authState = traktAuthRepository.getAuthState()
        if (authState == null || !authState.isAuthorized) return

        followedShowsRepository.addFollowedShow(showTraktId)

        processPendingUploads()
        processPendingDeletes()

        if (forceRefresh || lastRequestStore.isShowRequestExpired(showTraktId)) {
            syncShowWatches(showTraktId)
            lastRequestStore.updateShowLastRequest(showTraktId)
        }
    }

    private suspend fun processPendingUploads() {
        val pending = dao.entriesByPendingAction(PendingAction.UPLOAD)

        if (pending.isEmpty()) return

        logger.debug(TAG, "Processing ${pending.size} pending uploads")

        val entries = pending.map { episode ->
            WatchedEpisodeEntry(
                id = episode.watched_id,
                showTraktId = episode.show_trakt_id.id,
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
            dao.updatePendingAction(episode.watched_id, PendingAction.NOTHING)
        }

        logger.debug(TAG, "Successfully uploaded ${pending.size} episodes")
    }

    private suspend fun processPendingDeletes() {
        val pending = dao.entriesByPendingAction(PendingAction.DELETE)

        if (pending.isEmpty()) return

        logger.debug(TAG, "Processing ${pending.size} pending deletes")

        val traktIds = pending.mapNotNull { it.trakt_id }
        if (traktIds.isNotEmpty()) {
            dataSource.removeEpisodeWatches(traktIds)
        }

        pending.forEach { episode ->
            dao.deleteById(episode.watched_id)
        }

        logger.debug(TAG, "Successfully deleted ${pending.size} episodes")
    }

    private suspend fun syncShowWatches(showTraktId: Long) {
        val remoteWatches = dataSource.getShowEpisodeWatches(showTraktId)

        if (remoteWatches.isEmpty()) {
            logger.debug(TAG, "No remote watches for show $showTraktId")
            return
        }

        logger.debug(TAG, "Found ${remoteWatches.size} remote watches for show $showTraktId")

        val uniqueSeasons = remoteWatches.map { it.seasonNumber }.distinct()
        uniqueSeasons.parallelForEach(concurrency = SEASON_CONCURRENCY) { seasonNumber ->
            currentCoroutineContext().ensureActive()
            ensureSeasonDetailsExist(showTraktId, seasonNumber)
        }

        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()

        remoteWatches.chunked(BATCH_SIZE).forEach { batch ->
            currentCoroutineContext().ensureActive()

            val entriesWithEpisodeIds = batch.map { remoteEntry ->
                val episode = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
                    showTraktId = showTraktId,
                    seasonNumber = remoteEntry.seasonNumber,
                    episodeNumber = remoteEntry.episodeNumber,
                )
                remoteEntry.copy(episodeId = episode?.episode_id?.id)
            }

            dao.upsertBatchFromTrakt(
                showTraktId = showTraktId,
                entries = entriesWithEpisodeIds,
                includeSpecials = includeSpecials,
            )
        }

        logger.debug(TAG, "Synced ${remoteWatches.size} episode watches for show $showTraktId")
    }

    private suspend fun ensureSeasonDetailsExist(showTraktId: Long, seasonNumber: Long) {
        val season = seasonsDao.getSeasonByShowAndNumber(showTraktId, seasonNumber) ?: return

        val hasEpisodes = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
            showTraktId = showTraktId,
            seasonNumber = seasonNumber,
            episodeNumber = 1,
        ) != null

        if (!hasEpisodes) {
            logger.debug(TAG, "Fetching season $seasonNumber details for show $showTraktId")
            seasonDetailsRepository.fetchSeasonDetails(
                param = SeasonDetailsParam(
                    showTraktId = showTraktId,
                    seasonId = season.season_id.id,
                    seasonNumber = seasonNumber,
                ),
                forceRefresh = false,
            )
        }
    }

    private companion object {
        const val TAG = "WatchedEpisodeSyncRepository"
        const val BATCH_SIZE = 50
        const val SEASON_CONCURRENCY = 1
    }
}
