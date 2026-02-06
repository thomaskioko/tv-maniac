package com.thomaskioko.tvmaniac.episodes.implementation.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction
import com.thomaskioko.tvmaniac.db.GetPreviousUnwatchedEpisodes
import com.thomaskioko.tvmaniac.db.GetWatchedEpisodes
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Clock

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchedEpisodeDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
    private val dateTimeProvider: DateTimeProvider,
) : WatchedEpisodeDao {

    override fun observeWatchedEpisodes(showTraktId: Long): Flow<List<GetWatchedEpisodes>> {
        return database.watchedEpisodesQueries
            .getWatchedEpisodes(Id(showTraktId))
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .catch { emit(emptyList()) }
    }

    override suspend fun markAsWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    ) {
        val timestamp = dateTimeProvider.nowMillis()
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val _ = database.followedShowsQueries.upsertIfNotExists(
                    traktId = Id(showTraktId),
                    tmdbId = null,
                    followedAt = timestamp,
                )
                val localEpisodeId = getEpisodeIdOrNull(showTraktId, seasonNumber, episodeNumber)
                val _ = database.watchedEpisodesQueries.markAsWatched(
                    show_trakt_id = Id(showTraktId),
                    episode_id = localEpisodeId?.let { Id(it) },
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = timestamp,
                    pending_action = PendingAction.UPLOAD.value,
                )
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    show_trakt_id = Id(showTraktId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                val _ = database.showMetadataQueries.incrementWatchedCount(
                    show_trakt_id = Id(showTraktId),
                )
            }
        }
    }

    override suspend fun markAsUnwatched(
        showTraktId: Long,
        episodeId: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val entry = database.watchedEpisodesQueries
                    .getEntryByShowAndEpisode(Id(showTraktId), Id(episodeId))
                    .executeAsOneOrNull()

                if (entry != null) {
                    if (entry.trakt_id != null) {
                        val _ = database.watchedEpisodesQueries.updatePendingActionByShowAndEpisode(
                            pending_action = PendingAction.DELETE.value,
                            show_trakt_id = Id(showTraktId),
                            episode_id = Id(episodeId),
                        )
                    } else {
                        val _ = database.watchedEpisodesQueries.markAsUnwatched(
                            show_trakt_id = Id(showTraktId),
                            episode_id = Id(episodeId),
                        )
                    }
                }
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    show_trakt_id = Id(showTraktId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                val _ = database.showMetadataQueries.recalculateCachedCounts(
                    show_trakt_id = Id(showTraktId),
                )
            }
        }
    }

    override fun observeSeasonWatchProgress(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<SeasonWatchProgress> {
        return combine(
            database.watchedEpisodesQueries
                .getWatchedEpisodesForSeason(Id(showTraktId), seasonNumber)
                .asFlow()
                .mapToList(dispatchers.databaseRead),
            database.watchedEpisodesQueries
                .getTotalEpisodesForSeason(Id(showTraktId), seasonNumber)
                .asFlow()
                .map { it.executeAsOne() },
        ) { watchedEpisodes, totalCount ->
            SeasonWatchProgress(
                showTraktId = showTraktId,
                seasonNumber = seasonNumber,
                watchedCount = watchedEpisodes.size,
                totalCount = totalCount.toInt(),
            )
        }.catch {
            emit(SeasonWatchProgress(showTraktId, seasonNumber, 0, 0))
        }
    }

    override fun observeShowWatchProgress(showTraktId: Long): Flow<ShowWatchProgress> {
        return combine(
            database.watchedEpisodesQueries
                .getWatchedEpisodesCountForShow(Id(showTraktId))
                .asFlow()
                .map { it.executeAsOne() },
            database.watchedEpisodesQueries
                .getTotalEpisodesForShow(Id(showTraktId))
                .asFlow()
                .map { it.executeAsOne() },
        ) { watchedCount, totalCount ->
            ShowWatchProgress(
                showTraktId = showTraktId,
                watchedCount = watchedCount.toInt(),
                totalCount = totalCount.toInt(),
            )
        }.catch {
            emit(ShowWatchProgress(showTraktId, 0, 0))
        }
    }

    override fun observeAllSeasonsWatchProgress(showTraktId: Long): Flow<List<SeasonWatchProgress>> {
        return database.watchedEpisodesQueries
            .getAllSeasonsWatchProgress(Id(showTraktId))
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { results ->
                results.map { result ->
                    SeasonWatchProgress(
                        showTraktId = showTraktId,
                        seasonNumber = result.season_number,
                        watchedCount = result.watched_count.toInt(),
                        totalCount = result.total_count.toInt(),
                    )
                }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun markSeasonAsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        episodes: List<EpisodeWatchParams>,
        includeSpecials: Boolean,
    ) {
        val timestamp = dateTimeProvider.nowMillis()
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val _ = database.followedShowsQueries.upsertIfNotExists(
                    traktId = Id(showTraktId),
                    tmdbId = null,
                    followedAt = timestamp,
                )
                episodes.forEach { episode ->
                    require(episode.seasonNumber == seasonNumber) {
                        "Episode ${episode.episodeId} - ${episode.episodeNumber} belongs to season ${episode.seasonNumber}, not $seasonNumber"
                    }
                    val episodeTimestamp = episode.watchedAt ?: timestamp
                    val _ = database.watchedEpisodesQueries.markAsWatched(
                        show_trakt_id = Id(showTraktId),
                        episode_id = Id(episode.episodeId),
                        season_number = episode.seasonNumber,
                        episode_number = episode.episodeNumber,
                        watched_at = episodeTimestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    show_trakt_id = Id(showTraktId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                val _ = database.showMetadataQueries.recalculateCachedCounts(
                    show_trakt_id = Id(showTraktId),
                )
            }
        }
    }

    override suspend fun markSeasonAsUnwatched(
        showTraktId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val _ =
                    database.watchedEpisodesQueries.deleteForSeason(Id(showTraktId), seasonNumber)
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    show_trakt_id = Id(showTraktId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                val _ = database.showMetadataQueries.recalculateCachedCounts(
                    show_trakt_id = Id(showTraktId),
                )
            }
        }
    }

    override suspend fun markSeasonAndPreviousAsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ) {
        val timestamp = dateTimeProvider.nowMillis()
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodesInPreviousSeasons = database.watchedEpisodesQueries
                    .getUnwatchedEpisodesInPreviousSeasons(
                        show_trakt_id = Id(showTraktId),
                        season_number = seasonNumber,
                        include_specials = if (includeSpecials) 1L else 0L,
                    )
                    .executeAsList()

                unwatchedEpisodesInPreviousSeasons.forEach { episode ->
                    val _ = database.watchedEpisodesQueries.markAsWatched(
                        show_trakt_id = Id(showTraktId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }

                val currentSeasonEpisodes = database.watchedEpisodesQueries
                    .getEpisodesForSeason(Id(showTraktId), seasonNumber)
                    .executeAsList()

                val _ = database.followedShowsQueries.upsertIfNotExists(
                    traktId = Id(showTraktId),
                    tmdbId = null,
                    followedAt = timestamp,
                )

                currentSeasonEpisodes.forEach { episode ->
                    val _ = database.watchedEpisodesQueries.markAsWatched(
                        show_trakt_id = Id(showTraktId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }

                val _ = database.showMetadataQueries.recalculateLastWatched(
                    show_trakt_id = Id(showTraktId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                val _ = database.showMetadataQueries.recalculateCachedCounts(
                    show_trakt_id = Id(showTraktId),
                )
            }
        }
    }

    override suspend fun markEpisodeAndPreviousAsWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    ) {
        val timestamp = dateTimeProvider.nowMillis()
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodes = getPreviousUnwatchedEpisodes(
                    showTraktId = showTraktId,
                    seasonNumber = seasonNumber,
                    episodeNumber = episodeNumber,
                    includeSpecials = includeSpecials,
                )

                unwatchedEpisodes.forEach { episode ->
                    val _ = database.watchedEpisodesQueries.markAsWatched(
                        show_trakt_id = Id(showTraktId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                        pending_action = PendingAction.UPLOAD.value,
                    )
                }

                val _ = database.followedShowsQueries.upsertIfNotExists(
                    traktId = Id(showTraktId),
                    tmdbId = null,
                    followedAt = timestamp,
                )
                val _ = database.watchedEpisodesQueries.markAsWatched(
                    show_trakt_id = Id(showTraktId),
                    episode_id = Id(episodeId),
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = timestamp,
                    pending_action = PendingAction.UPLOAD.value,
                )
                val _ = database.showMetadataQueries.recalculateLastWatched(
                    show_trakt_id = Id(showTraktId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                val _ = database.showMetadataQueries.recalculateCachedCounts(
                    show_trakt_id = Id(showTraktId),
                )
            }
        }
    }

    private fun getPreviousUnwatchedEpisodes(
        showTraktId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    ): List<GetPreviousUnwatchedEpisodes> {
        val unwatchedEpisodes = database.watchedEpisodesQueries
            .getPreviousUnwatchedEpisodes(
                show_trakt_id = Id(showTraktId),
                season_number = seasonNumber,
                episode_number = episodeNumber,
                include_specials = if (includeSpecials) 1L else 0L,
            )
            .executeAsList()
        return unwatchedEpisodes
    }

    private fun getEpisodeIdOrNull(
        showTraktId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Long? {
        return database.episodesQueries
            .getEpisodeByShowSeasonEpisodeNumber(
                showTraktId = Id(showTraktId),
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
            )
            .executeAsOneOrNull()
            ?.episode_id
            ?.id
    }

    override suspend fun getEpisodesForSeason(
        showTraktId: Long,
        seasonNumber: Long,
    ): List<EpisodeWatchParams> {
        return withContext(dispatchers.databaseRead) {
            database.watchedEpisodesQueries
                .getEpisodesForSeason(Id(showTraktId), seasonNumber)
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
        showTraktId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Long {
        return withContext(dispatchers.databaseRead) {
            database.watchedEpisodesQueries
                .getUnwatchedEpisodeCountInPreviousSeasons(
                    show_trakt_id = Id(showTraktId),
                    season_number = seasonNumber,
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                .executeAsOne()
        }
    }

    override fun observeUnwatchedCountInPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Flow<Long> {
        return database.watchedEpisodesQueries
            .getUnwatchedEpisodeCountInPreviousSeasons(
                show_trakt_id = Id(showTraktId),
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
        showTraktId: Long,
        entries: List<WatchedEpisodeEntry>,
        includeSpecials: Boolean,
    ) {
        if (entries.isEmpty()) return

        val syncedAt = Clock.System.now().toEpochMilliseconds()

        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val _ = database.followedShowsQueries.upsertIfNotExists(
                    traktId = Id(showTraktId),
                    tmdbId = null,
                    followedAt = entries.first().watchedAt.toEpochMilliseconds(),
                )

                entries.forEach { entry ->
                    val _ = database.watchedEpisodesQueries.upsertFromTrakt(
                        show_trakt_id = Id(showTraktId),
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
                    show_trakt_id = Id(showTraktId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                val _ = database.showMetadataQueries.recalculateCachedCounts(
                    show_trakt_id = Id(showTraktId),
                )
            }
        }
    }
}
