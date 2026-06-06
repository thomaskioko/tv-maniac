package com.thomaskioko.tvmaniac.episodes.implementation.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction
import com.thomaskioko.tvmaniac.db.GetPreviousUnwatchedEpisodes
import com.thomaskioko.tvmaniac.db.GetWatchedEpisodes
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchedEpisodeDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
    private val dateTimeProvider: DateTimeProvider,
) : WatchedEpisodeDao {

    override fun observeWatchedEpisodes(showId: Long): Flow<List<GetWatchedEpisodes>> {
        val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return flowOf(emptyList())
        return database.watchedEpisodesQueries
            .getWatchedEpisodes(internalShowId)
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .catch { emit(emptyList()) }
    }

    override fun observeRecentlyWatched(limit: Long): Flow<List<RecentlyWatchedEpisode>> {
        return database.watchedEpisodesQueries
            .getRecentlyWatched(limit)
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { rows ->
                rows.map { row ->
                    RecentlyWatchedEpisode(
                        showId = row.show_trakt_id,
                        showTmdbId = row.show_tmdb_id.id,
                        showTitle = row.show_title,
                        posterPath = row.poster_path,
                        seasonNumber = row.season_number,
                        episodeNumber = row.episode_number,
                        episodeTitle = row.episode_title,
                        watchedAt = row.watched_at,
                    )
                }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun markAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    ) {
        val timestamp = dateTimeProvider.nowMillis()
        withContext(dispatchers.databaseWrite) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext
            database.transaction {
                val _ = database.followedShowsQueries.upsertIfNotExists(
                    showId = internalShowId,
                    tmdbId = null,
                    followedAt = timestamp,
                )
                val _ = database.continueWatchingQueries.upsertMembershipForLocalMark(
                    showId = internalShowId,
                    tmdbId = null,
                    watchedAt = timestamp,
                )
                val localEpisodeId = getEpisodeIdOrNull(internalShowId, seasonNumber, episodeNumber)
                val _ = database.watchedEpisodesQueries.markAsWatched(
                    show_id = internalShowId,
                    episode_id = localEpisodeId?.let { Id(it) },
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = timestamp,
                    pending_action = PendingAction.UPLOAD.value,
                )
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    showId = internalShowId,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun markAsUnwatched(
        showId: Long,
        episodeId: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext
            database.transaction {
                val entry = database.watchedEpisodesQueries
                    .getEntryByShowAndEpisode(internalShowId, Id(episodeId))
                    .executeAsOneOrNull()

                if (entry != null) {
                    if (entry.trakt_id != null) {
                        val _ = database.watchedEpisodesQueries.updatePendingActionByShowAndEpisode(
                            pending_action = PendingAction.DELETE.value,
                            show_id = internalShowId,
                            episode_id = Id(episodeId),
                        )
                    } else {
                        val _ = database.watchedEpisodesQueries.markAsUnwatched(
                            show_id = internalShowId,
                            episode_id = Id(episodeId),
                        )
                    }
                }
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    showId = internalShowId,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                recalculateContinueWatchingLastWatchedAt(internalShowId)
            }
        }
    }

    override fun observeSeasonWatchProgress(
        showId: Long,
        seasonNumber: Long,
    ): Flow<SeasonWatchProgress> {
        val internalShowId = showIdResolver.showIdForTraktId(showId)
            ?: return flowOf(SeasonWatchProgress(showId, seasonNumber, 0, 0))
        return combine(
            database.watchedEpisodesQueries
                .getWatchedEpisodesForSeason(internalShowId, seasonNumber)
                .asFlow()
                .mapToList(dispatchers.databaseRead),
            database.watchedEpisodesQueries
                .getTotalEpisodesForSeason(internalShowId, seasonNumber)
                .asFlow()
                .map { it.executeAsOne() },
        ) { watchedEpisodes, totalCount ->
            SeasonWatchProgress(
                showId = showId,
                seasonNumber = seasonNumber,
                watchedCount = watchedEpisodes.size,
                totalCount = totalCount.toInt(),
            )
        }.catch {
            emit(SeasonWatchProgress(showId, seasonNumber, 0, 0))
        }
    }

    override fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress> {
        val internalShowId = showIdResolver.showIdForTraktId(showId)
            ?: return flowOf(ShowWatchProgress(showId, 0, 0))
        return combine(
            database.watchedEpisodesQueries
                .getWatchedEpisodesCountForShow(internalShowId)
                .asFlow()
                .map { it.executeAsOne() },
            database.watchedEpisodesQueries
                .getTotalEpisodesForShow(internalShowId)
                .asFlow()
                .map { it.executeAsOne() },
        ) { watchedCount, totalCount ->
            ShowWatchProgress(
                showId = showId,
                watchedCount = watchedCount.toInt(),
                totalCount = totalCount.toInt(),
            )
        }
    }

    override fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>> {
        val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return flowOf(emptyList())
        return database.watchedEpisodesQueries
            .getAllSeasonsWatchProgress(internalShowId)
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { results ->
                results.map { result ->
                    SeasonWatchProgress(
                        showId = showId,
                        seasonNumber = result.season_number,
                        watchedCount = result.watched_count.toInt(),
                        totalCount = result.total_count.toInt(),
                    )
                }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun markSeasonAsWatched(
        showId: Long,
        seasonNumber: Long,
        episodes: List<EpisodeWatchParams>,
        includeSpecials: Boolean,
    ) {
        val timestamp = dateTimeProvider.nowMillis()
        withContext(dispatchers.databaseWrite) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext
            database.transaction {
                val _ = database.followedShowsQueries.upsertIfNotExists(
                    showId = internalShowId,
                    tmdbId = null,
                    followedAt = timestamp,
                )
                val _ = database.continueWatchingQueries.upsertMembershipForLocalMark(
                    showId = internalShowId,
                    tmdbId = null,
                    watchedAt = timestamp,
                )
                episodes.forEach { episode ->
                    require(episode.seasonNumber == seasonNumber) {
                        "Episode ${episode.episodeId} - ${episode.episodeNumber} belongs to season ${episode.seasonNumber}, not $seasonNumber"
                    }
                    val episodeTimestamp = episode.watchedAt ?: timestamp
                    val _ = database.watchedEpisodesQueries.markAsWatched(
                        show_id = internalShowId,
                        episode_id = Id(episode.episodeId),
                        season_number = episode.seasonNumber,
                        episode_number = episode.episodeNumber,
                        watched_at = episodeTimestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    showId = internalShowId,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun markSeasonAsUnwatched(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext
            database.transaction {
                val seasonRows = database.watchedEpisodesQueries
                    .getWatchedEpisodesForSeason(internalShowId, seasonNumber)
                    .executeAsList()

                seasonRows.forEach { row ->
                    if (row.trakt_id != null) {
                        val _ = database.watchedEpisodesQueries.updatePendingAction(
                            pending_action = PendingAction.DELETE.value,
                            id = row.watched_id,
                        )
                    } else {
                        val _ = database.watchedEpisodesQueries.deleteById(row.watched_id)
                    }
                }
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    showId = internalShowId,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                recalculateContinueWatchingLastWatchedAt(internalShowId)
            }
        }
    }

    override suspend fun markAsSyncedDelete(id: Long) {
        withContext(dispatchers.databaseWrite) {
            database.watchedEpisodesQueries.markAsSyncedDelete(
                now = dateTimeProvider.nowMillis(),
                id = id,
            )
        }
    }

    override suspend fun purgeSyncedDeletesOlderThan(thresholdMillis: Long) {
        withContext(dispatchers.databaseWrite) {
            database.watchedEpisodesQueries.purgeSyncedDeletesOlderThan(threshold = thresholdMillis)
        }
    }

    override suspend fun markSeasonAndPreviousAsWatched(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ) {
        val timestamp = dateTimeProvider.nowMillis()
        withContext(dispatchers.databaseWrite) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext
            database.transaction {
                val unwatchedEpisodesInPreviousSeasons = database.watchedEpisodesQueries
                    .getUnwatchedEpisodesInPreviousSeasons(
                        showId = internalShowId,
                        season_number = seasonNumber,
                        include_specials = if (includeSpecials) 1L else 0L,
                    )
                    .executeAsList()

                unwatchedEpisodesInPreviousSeasons.forEach { episode ->
                    val _ = database.watchedEpisodesQueries.markAsWatched(
                        show_id = internalShowId,
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }

                val currentSeasonEpisodes = database.watchedEpisodesQueries
                    .getEpisodesForSeason(internalShowId, seasonNumber)
                    .executeAsList()

                val _ = database.followedShowsQueries.upsertIfNotExists(
                    showId = internalShowId,
                    tmdbId = null,
                    followedAt = timestamp,
                )
                val _ = database.continueWatchingQueries.upsertMembershipForLocalMark(
                    showId = internalShowId,
                    tmdbId = null,
                    watchedAt = timestamp,
                )

                currentSeasonEpisodes.forEach { episode ->
                    val _ = database.watchedEpisodesQueries.markAsWatched(
                        show_id = internalShowId,
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }

                val _ = database.showMetadataQueries.recalculateLastWatched(
                    showId = internalShowId,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun markEpisodeAndPreviousAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    ) {
        val timestamp = dateTimeProvider.nowMillis()
        withContext(dispatchers.databaseWrite) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext
            database.transaction {
                val unwatchedEpisodes = getPreviousUnwatchedEpisodes(
                    showId = internalShowId,
                    seasonNumber = seasonNumber,
                    episodeNumber = episodeNumber,
                    includeSpecials = includeSpecials,
                )

                unwatchedEpisodes.forEach { episode ->
                    val _ = database.watchedEpisodesQueries.markAsWatched(
                        show_id = internalShowId,
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }

                val _ = database.followedShowsQueries.upsertIfNotExists(
                    showId = internalShowId,
                    tmdbId = null,
                    followedAt = timestamp,
                )
                val _ = database.continueWatchingQueries.upsertMembershipForLocalMark(
                    showId = internalShowId,
                    tmdbId = null,
                    watchedAt = timestamp,
                )
                val _ = database.watchedEpisodesQueries.markAsWatched(
                    show_id = internalShowId,
                    episode_id = Id(episodeId),
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = timestamp,
                    pending_action = PendingAction.UPLOAD.value,
                )
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    showId = internalShowId,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    private fun getPreviousUnwatchedEpisodes(
        showId: Id<ShowId>,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    ): List<GetPreviousUnwatchedEpisodes> {
        val unwatchedEpisodes = database.watchedEpisodesQueries
            .getPreviousUnwatchedEpisodes(
                showId = showId,
                season_number = seasonNumber,
                episode_number = episodeNumber,
                include_specials = if (includeSpecials) 1L else 0L,
            )
            .executeAsList()
        return unwatchedEpisodes
    }

    private fun getEpisodeIdOrNull(
        showId: Id<ShowId>,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Long? {
        return database.episodesQueries
            .getEpisodeByShowSeasonEpisodeNumber(
                showId = showId,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
            )
            .executeAsOneOrNull()
            ?.episode_id
            ?.id
    }

    private fun recalculateContinueWatchingLastWatchedAt(showId: Id<ShowId>) {
        val maxWatchedAt = database.watchedEpisodesQueries
            .getMaxWatchedAtForShow(showId)
            .executeAsOne()
            .last_watched_at
            ?: return
        database.continueWatchingQueries.updateLastWatchedAt(
            lastWatchedAt = maxWatchedAt,
            showId = showId,
        )
    }

    override suspend fun getEpisodesForSeason(
        showId: Long,
        seasonNumber: Long,
    ): List<EpisodeWatchParams> {
        return withContext(dispatchers.databaseRead) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext emptyList()
            database.watchedEpisodesQueries
                .getEpisodesForSeason(internalShowId, seasonNumber)
                .executeAsList()
                .map { result ->
                    EpisodeWatchParams(
                        episodeId = result.episode_id.id,
                        seasonNumber = result.season_number,
                        episodeNumber = result.episode_number,
                    )
                }
        }
    }

    override suspend fun getUnwatchedEpisodeCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Long {
        return withContext(dispatchers.databaseRead) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext 0L
            database.watchedEpisodesQueries
                .getUnwatchedEpisodeCountInPreviousSeasons(
                    showId = internalShowId,
                    season_number = seasonNumber,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                .executeAsOne()
        }
    }

    override fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Flow<Long> {
        val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return flowOf(0L)
        return database.watchedEpisodesQueries
            .getUnwatchedEpisodeCountInPreviousSeasons(
                showId = internalShowId,
                season_number = seasonNumber,
                include_specials = if (includeSpecials) 1L else 0L,
            )
            .asFlow()
            .map { it.executeAsOne() }
            .catch { emit(0L) }
    }

    override suspend fun entriesByPendingAction(action: PendingAction): List<GetEntriesByPendingAction> {
        return withContext(dispatchers.databaseRead) {
            database.watchedEpisodesQueries
                .getEntriesByPendingAction(action.value)
                .executeAsList()
        }
    }

    override suspend fun updatePendingAction(id: Long, action: PendingAction) {
        withContext(dispatchers.databaseWrite) {
            database.watchedEpisodesQueries.updatePendingAction(action.value, id)
        }
    }

    override suspend fun deleteById(id: Long) {
        withContext(dispatchers.databaseWrite) {
            database.watchedEpisodesQueries.deleteById(id)
        }
    }

    override suspend fun upsertBatchFromTrakt(
        showId: Long,
        entries: List<WatchedEpisodeEntry>,
        includeSpecials: Boolean,
    ) {
        if (entries.isEmpty()) return

        val syncedAt = Clock.System.now().toEpochMilliseconds()

        withContext(dispatchers.databaseWrite) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext
            database.transaction {
                val showExists = database.tvShowQueries
                    .existsByShowId(showId)
                    .executeAsOne()
                if (!showExists) return@transaction

                val _ = database.followedShowsQueries.upsertIfNotExists(
                    showId = internalShowId,
                    tmdbId = null,
                    followedAt = entries.first().watchedAt.toEpochMilliseconds(),
                )

                entries.forEach { entry ->
                    val _ = database.watchedEpisodesQueries.upsertFromTrakt(
                        show_id = internalShowId,
                        episode_id = entry.episodeId?.let { Id(it) },
                        season_number = entry.seasonNumber,
                        episode_number = entry.episodeNumber,
                        watched_at = entry.watchedAt.toEpochMilliseconds(),
                        trakt_id = entry.traktId,
                        synced_at = syncedAt,
                        pending_action = PendingAction.NOTHING.value,
                    )
                }

                val _ = database.showMetadataQueries.recalculateLastWatched(
                    showId = internalShowId,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }
}
