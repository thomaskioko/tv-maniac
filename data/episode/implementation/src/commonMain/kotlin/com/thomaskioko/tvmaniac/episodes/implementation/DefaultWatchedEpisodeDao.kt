package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.GetPreviousUnwatchedEpisodes
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchedEpisodeDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : WatchedEpisodeDao {

    override fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>> {
        return database.watchedEpisodesQueries
            .getWatchedEpisodes(Id(showId))
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .catch { emit(emptyList()) }
    }

    override suspend fun markAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.followedShowsQueries.upsertIfNotExists(
                    tmdbId = showId,
                    followedAt = watchedAt,
                )
                database.watchedEpisodesQueries.markAsWatched(
                    show_id = Id(showId),
                    episode_id = Id(episodeId),
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = watchedAt,
                    pending_action = PendingAction.UPLOAD.value,
                )
                database.showMetadataQueries.recalculateLastWatched(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun markAsUnwatched(showId: Long, episodeId: Long, includeSpecials: Boolean) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val entry = database.watchedEpisodesQueries
                    .getEntryByShowAndEpisode(Id(showId), Id(episodeId))
                    .executeAsOneOrNull()

                if (entry != null) {
                    if (entry.trakt_id != null) {
                        database.watchedEpisodesQueries.updatePendingActionByShowAndEpisode(
                            pending_action = PendingAction.DELETE.value,
                            show_id = Id(showId),
                            episode_id = Id(episodeId),
                        )
                    } else {
                        database.watchedEpisodesQueries.markAsUnwatched(
                            show_id = Id(showId),
                            episode_id = Id(episodeId),
                        )
                    }
                }
                database.showMetadataQueries.recalculateLastWatched(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun deleteAllForShow(showId: Long) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchedEpisodesQueries.deleteAllForShow(Id(showId))
                database.showMetadataQueries.clearLastWatched(Id(showId))
            }
        }
    }

    override fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress> {
        return combine(
            database.watchedEpisodesQueries
                .getWatchedEpisodesForSeason(Id(showId), seasonNumber)
                .asFlow()
                .mapToList(dispatchers.databaseRead),
            database.watchedEpisodesQueries
                .getTotalEpisodesForSeason(Id(showId), seasonNumber)
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
        return combine(
            database.watchedEpisodesQueries
                .getWatchedEpisodesCountForShow(Id(showId))
                .asFlow()
                .map { it.executeAsOne() },
            database.watchedEpisodesQueries
                .getTotalEpisodesForShow(Id(showId))
                .asFlow()
                .map { it.executeAsOne() },
        ) { watchedCount, totalCount ->
            ShowWatchProgress(
                showId = showId,
                watchedCount = watchedCount.toInt(),
                totalCount = totalCount.toInt(),
            )
        }.catch {
            emit(ShowWatchProgress(showId, 0, 0))
        }
    }

    override fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>> {
        return database.watchedEpisodesQueries
            .getAllSeasonsWatchProgress(Id(showId))
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
        timestamp: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.followedShowsQueries.upsertIfNotExists(
                    tmdbId = showId,
                    followedAt = timestamp,
                )
                episodes.forEach { episode ->
                    require(episode.seasonNumber == seasonNumber) {
                        "Episode ${episode.episodeId} - ${episode.episodeNumber} belongs to season ${episode.seasonNumber}, not $seasonNumber"
                    }
                    val episodeTimestamp = episode.watchedAt ?: timestamp
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = Id(episode.episodeId),
                        season_number = episode.seasonNumber,
                        episode_number = episode.episodeNumber,
                        watched_at = episodeTimestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }
                database.showMetadataQueries.recalculateLastWatched(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun markSeasonAsUnwatched(showId: Long, seasonNumber: Long, includeSpecials: Boolean) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchedEpisodesQueries.deleteForSeason(Id(showId), seasonNumber)
                database.showMetadataQueries.recalculateLastWatched(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun markPreviousSeasonsAsWatched(
        showId: Long,
        seasonNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodes = database.watchedEpisodesQueries
                    .getUnwatchedEpisodesInPreviousSeasons(
                        show_id = Id(showId),
                        season_number = seasonNumber,
                        include_specials = if (includeSpecials) 1L else 0L,
                    )
                    .executeAsList()

                unwatchedEpisodes.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }
                database.showMetadataQueries.recalculateLastWatched(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun markSeasonAndPreviousAsWatched(
        showId: Long,
        seasonNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodesInPreviousSeasons = database.watchedEpisodesQueries
                    .getUnwatchedEpisodesInPreviousSeasons(
                        show_id = Id(showId),
                        season_number = seasonNumber,
                        include_specials = if (includeSpecials) 1L else 0L,
                    )
                    .executeAsList()

                unwatchedEpisodesInPreviousSeasons.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }

                val currentSeasonEpisodes = database.watchedEpisodesQueries
                    .getEpisodesForSeason(Id(showId), seasonNumber)
                    .executeAsList()

                database.followedShowsQueries.upsertIfNotExists(
                    tmdbId = showId,
                    followedAt = timestamp,
                )

                currentSeasonEpisodes.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }

                database.showMetadataQueries.recalculateLastWatched(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun markPreviousEpisodesAsWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodes = getPreviousUnwatchedEpisodes(
                    showId = showId,
                    seasonNumber = seasonNumber,
                    episodeNumber = episodeNumber,
                    includeSpecials = includeSpecials
                )

                unwatchedEpisodes.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }
                database.showMetadataQueries.recalculateLastWatched(
                    show_id = Id(showId),
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
        timestamp: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodes = getPreviousUnwatchedEpisodes(
                    showId = showId,
                    seasonNumber = seasonNumber,
                    episodeNumber = episodeNumber,
                    includeSpecials = includeSpecials
                )

                unwatchedEpisodes.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }

                database.followedShowsQueries.upsertIfNotExists(
                    tmdbId = showId,
                    followedAt = timestamp,
                )
                database.watchedEpisodesQueries.markAsWatched(
                    show_id = Id(showId),
                    episode_id = Id(episodeId),
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = timestamp,
                    pending_action = PendingAction.UPLOAD.value,
                )
                database.showMetadataQueries.recalculateLastWatched(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    private fun getPreviousUnwatchedEpisodes(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    ): List<GetPreviousUnwatchedEpisodes> {
        val unwatchedEpisodes = database.watchedEpisodesQueries
            .getPreviousUnwatchedEpisodes(
                show_id = Id(showId),
                season_number = seasonNumber,
                episode_number = episodeNumber,
                include_specials = if (includeSpecials) 1L else 0L,
            )
            .executeAsList()
        return unwatchedEpisodes
    }

    override suspend fun getEpisodesForSeason(
        showId: Long,
        seasonNumber: Long,
    ): List<EpisodeWatchParams> {
        return withContext(dispatchers.databaseRead) {
            database.watchedEpisodesQueries
                .getEpisodesForSeason(Id(showId), seasonNumber)
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
            database.watchedEpisodesQueries
                .getUnwatchedEpisodeCountInPreviousSeasons(
                    show_id = Id(showId),
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
        return database.watchedEpisodesQueries
            .getUnwatchedEpisodeCountInPreviousSeasons(
                show_id = Id(showId),
                season_number = seasonNumber,
                include_specials = if (includeSpecials) 1L else 0L,
            )
            .asFlow()
            .map { it.executeAsOne() }
            .catch { emit(0L) }
    }

    override suspend fun entriesByPendingAction(action: PendingAction): List<Watched_episodes> {
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

    override suspend fun upsertFromTrakt(
        showId: Long,
        episodeId: Long?,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
        traktId: Long,
        syncedAt: Long,
        pendingAction: String,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.followedShowsQueries.upsertIfNotExists(
                    tmdbId = showId,
                    followedAt = watchedAt,
                )
                database.watchedEpisodesQueries.upsertFromTrakt(
                    show_id = Id(showId),
                    episode_id = episodeId?.let { Id(it) },
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = watchedAt,
                    trakt_id = traktId,
                    synced_at = syncedAt,
                    pending_action = pendingAction,
                )
                database.showMetadataQueries.recalculateLastWatched(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }

    override suspend fun upsert(entry: Watched_episodes, includeSpecials: Boolean) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.followedShowsQueries.upsertIfNotExists(
                    tmdbId = entry.show_id.id,
                    followedAt = entry.watched_at,
                )
                database.watchedEpisodesQueries.upsert(
                    show_id = entry.show_id,
                    episode_id = entry.episode_id,
                    season_number = entry.season_number,
                    episode_number = entry.episode_number,
                    watched_at = entry.watched_at,
                    pending_action = entry.pending_action,
                )
                database.showMetadataQueries.recalculateLastWatched(
                    show_id = entry.show_id,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
            }
        }
    }
}
