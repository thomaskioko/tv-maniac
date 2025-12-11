package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UnwatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.absoluteEpisodeNumber
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
    private val nextEpisodeDao: NextEpisodeDao,
    private val dispatchers: AppCoroutineDispatchers,
) : WatchedEpisodeDao {

    override fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>> {
        return database.watchedEpisodesQueries
            .getWatchedEpisodes(Id(showId))
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .catch { emit(emptyList()) }
    }

    override fun observeWatchProgress(showId: Long): Flow<WatchProgress> {
        return combine(
            observeWatchedEpisodes(showId),
            nextEpisodeDao.observeNextEpisode(showId),
        ) { watchedEpisodes, nextEpisode ->
            val lastWatched = watchedEpisodes.maxByOrNull { it.absoluteEpisodeNumber() }

            WatchProgress(
                showId = showId,
                totalEpisodesWatched = watchedEpisodes.size,
                lastSeasonWatched = lastWatched?.season_number,
                lastEpisodeWatched = lastWatched?.episode_number,
                nextEpisode = nextEpisode,
            )
        }
    }

    override suspend fun markAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchlistQueries.upsertIfNotExists(
                    id = Id(showId),
                    created_at = watchedAt,
                )
                database.watchedEpisodesQueries.markAsWatched(
                    show_id = Id(showId),
                    episode_id = Id(episodeId),
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = watchedAt,
                )
            }
        }
    }

    override suspend fun markAsUnwatched(showId: Long, episodeId: Long) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchedEpisodesQueries.markAsUnwatched(
                    show_id = Id(showId),
                    episode_id = Id(episodeId),
                )
            }
        }
    }

    override suspend fun getLastWatchedEpisode(showId: Long): Watched_episodes? {
        return withContext(dispatchers.databaseRead) {
            database.watchedEpisodesQueries
                .getLastWatchedEpisode(Id(showId))
                .executeAsOneOrNull()
        }
    }

    override suspend fun getWatchedEpisodesForSeason(
        showId: Long,
        seasonNumber: Long,
    ): List<Watched_episodes> {
        return withContext(dispatchers.databaseRead) {
            database.watchedEpisodesQueries
                .getWatchedEpisodesForSeason(Id(showId), seasonNumber)
                .executeAsList()
        }
    }

    override suspend fun isEpisodeWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Boolean {
        return withContext(dispatchers.databaseRead) {
            database.watchedEpisodesQueries
                .isEpisodeWatched(Id(showId), seasonNumber, episodeNumber)
                .executeAsOne()
        }
    }

    override suspend fun deleteAllForShow(showId: Long) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchedEpisodesQueries.deleteAllForShow(Id(showId))
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

    override suspend fun markSeasonAsWatched(
        showId: Long,
        seasonNumber: Long,
        episodes: List<EpisodeWatchParams>,
        timestamp: Long,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchlistQueries.upsertIfNotExists(
                    id = Id(showId),
                    created_at = timestamp,
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
                    )
                }
            }
        }
    }

    override suspend fun markSeasonAsUnwatched(showId: Long, seasonNumber: Long) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchedEpisodesQueries.deleteForSeason(Id(showId), seasonNumber)
            }
        }
    }

    override suspend fun markPreviousSeasonsAsWatched(
        showId: Long,
        seasonNumber: Long,
        timestamp: Long,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodes = database.watchedEpisodesQueries
                    .getUnwatchedEpisodesInPreviousSeasons(Id(showId), seasonNumber)
                    .executeAsList()

                unwatchedEpisodes.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                    )
                }
            }
        }
    }

    override suspend fun markSeasonAndPreviousAsWatched(
        showId: Long,
        seasonNumber: Long,
        timestamp: Long,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodesInPreviousSeasons = database.watchedEpisodesQueries
                    .getUnwatchedEpisodesInPreviousSeasons(Id(showId), seasonNumber)
                    .executeAsList()

                unwatchedEpisodesInPreviousSeasons.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                    )
                }

                val currentSeasonEpisodes = database.watchedEpisodesQueries
                    .getEpisodesForSeason(Id(showId), seasonNumber)
                    .executeAsList()

                database.watchlistQueries.upsertIfNotExists(
                    id = Id(showId),
                    created_at = timestamp,
                )

                currentSeasonEpisodes.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                    )
                }
            }
        }
    }

    override suspend fun markPreviousEpisodesAsWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        timestamp: Long,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodes = database.watchedEpisodesQueries
                    .getUnwatchedEpisodesBefore(Id(showId), seasonNumber, episodeNumber)
                    .executeAsList()

                unwatchedEpisodes.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                    )
                }
            }
        }
    }

    override suspend fun markEpisodeAndPreviousAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        timestamp: Long,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                val unwatchedEpisodes = database.watchedEpisodesQueries
                    .getUnwatchedEpisodesBefore(Id(showId), seasonNumber, episodeNumber)
                    .executeAsList()

                unwatchedEpisodes.forEach { episode ->
                    database.watchedEpisodesQueries.markAsWatched(
                        show_id = Id(showId),
                        episode_id = episode.episode_id,
                        season_number = episode.season_number,
                        episode_number = episode.episode_number,
                        watched_at = timestamp,
                    )
                }

                database.watchlistQueries.upsertIfNotExists(
                    id = Id(showId),
                    created_at = timestamp,
                )
                database.watchedEpisodesQueries.markAsWatched(
                    show_id = Id(showId),
                    episode_id = Id(episodeId),
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = timestamp,
                )
            }
        }
    }

    override suspend fun getUnwatchedEpisodesBefore(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): List<UnwatchedEpisode> {
        return withContext(dispatchers.databaseRead) {
            database.watchedEpisodesQueries
                .getUnwatchedEpisodesBefore(
                    show_id = Id(showId),
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                )
                .executeAsList()
                .map { result ->
                    UnwatchedEpisode(
                        episodeId = result.episode_id.id,
                        seasonNumber = result.season_number,
                        episodeNumber = result.episode_number,
                        seasonId = result.season_id.id,
                    )
                }
        }
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
    ): Long {
        return withContext(dispatchers.databaseRead) {
            database.watchedEpisodesQueries
                .getUnwatchedEpisodeCountInPreviousSeasons(Id(showId), seasonNumber)
                .executeAsOne()
        }
    }

    override fun observeUnsyncedEpisodes(): Flow<List<Watched_episodes>> {
        return database.watchedEpisodesQueries
            .getUnsyncedEpisodes()
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .catch { emit(emptyList()) }
    }

    override suspend fun updateSyncStatus(id: Long, status: String, syncedAt: Long) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchedEpisodesQueries.updateSyncStatus(status, syncedAt, id)
            }
        }
    }

    override suspend fun upsertFromTrakt(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
        traktId: Long,
        syncedAt: Long,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchlistQueries.upsertIfNotExists(
                    id = Id(showId),
                    created_at = watchedAt,
                )
                database.watchedEpisodesQueries.upsertFromTrakt(
                    show_id = Id(showId),
                    episode_id = Id(episodeId),
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = watchedAt,
                    trakt_id = traktId,
                    synced_at = syncedAt,
                )
            }
        }
    }
}
