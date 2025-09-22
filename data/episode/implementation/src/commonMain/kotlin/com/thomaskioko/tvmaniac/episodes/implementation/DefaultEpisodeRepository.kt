package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.model.LastWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgressContext
import com.thomaskioko.tvmaniac.episodes.api.model.absoluteEpisodeNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultEpisodeRepository(
    private val watchedEpisodeDao: WatchedEpisodeDao,
    private val nextEpisodeDao: DefaultNextEpisodeDao,
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : EpisodeRepository {

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        nextEpisodeDao.observeNextEpisodesForWatchlist()

    override fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?> =
        nextEpisodeDao.observeNextEpisode(showId)

    override suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        watchedEpisodeDao.markAsWatched(showId, episodeId, seasonNumber, episodeNumber)
    }

    override suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long) {
        watchedEpisodeDao.markAsUnwatched(showId, episodeId)
    }

    override fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>> =
        watchedEpisodeDao.observeWatchedEpisodes(showId)

    override fun observeWatchProgress(showId: Long): Flow<WatchProgress> =
        watchedEpisodeDao.observeWatchProgress(showId)

    override suspend fun getLastWatchedEpisode(showId: Long): Watched_episodes? =
        watchedEpisodeDao.getLastWatchedEpisode(showId)

    override suspend fun isEpisodeWatched(showId: Long, seasonNumber: Long, episodeNumber: Long): Boolean =
        watchedEpisodeDao.isEpisodeWatched(showId, seasonNumber, episodeNumber)

    override suspend fun clearWatchHistoryForShow(showId: Long) {
        watchedEpisodeDao.deleteAllForShow(showId)
    }

    override suspend fun getWatchProgressContext(showId: Long): WatchProgressContext {
        return withContext(dispatchers.databaseRead) {
            // Get total episodes by querying the episode table directly and filtering by season
            val totalEpisodes = database.transactionWithResult {
                database.seasonsQueries
                    .showSeasons(Id(showId))
                    .executeAsList()
                    .filter { it.season_number > 0 || (it.season_number == 0L && it.season_title != "Specials") }
                    .sumOf { season ->
                        database.seasonsQueries
                            .seasonDetails(showId = Id(showId), seasonNumber = season.season_number)
                            .executeAsList()
                            .count { it.episode_id != null } // Count actual episodes
                    }
            }

            val watchedEpisodesList = database.watchedEpisodesQueries
                .getWatchedEpisodes(Id(showId))
                .executeAsList()

            val watchedEpisodesCount = watchedEpisodesList.size

            val lastWatched = database.showsLastWatchedQueries
                .lastWatchedEpisodeForShow(Id(showId))
                .executeAsOneOrNull()

            val progressPercentage = if (totalEpisodes > 0) {
                (watchedEpisodesCount.toFloat() / totalEpisodes.toFloat()) * 100f
            } else {
                0f
            }

            val isOutOfOrder = isWatchingOutOfOrder(showId)
            val hasEarlierUnwatched = hasUnwatchedEarlierEpisodes(showId)

            WatchProgressContext(
                showId = showId,
                totalEpisodes = totalEpisodes,
                watchedEpisodes = watchedEpisodesCount,
                lastWatchedSeasonNumber = lastWatched?.last_watched_season?.toInt(),
                lastWatchedEpisodeNumber = lastWatched?.last_watched_episode?.toInt(),
                nextEpisode = null,
                isWatchingOutOfOrder = isOutOfOrder,
                hasUnwatchedEarlierEpisodes = hasEarlierUnwatched,
                progressPercentage = progressPercentage,
            )
        }
    }

    override suspend fun hasUnwatchedEarlierEpisodes(showId: Long): Boolean {
        return withContext(dispatchers.databaseRead) {
            val watchedEpisodes = database.watchedEpisodesQueries
                .getWatchedEpisodes(Id(showId))
                .executeAsList()

            val lastWatched = watchedEpisodes.maxByOrNull { it.absoluteEpisodeNumber() }
                ?: return@withContext false

            val allEpisodes = database.transactionWithResult {
                val seasons = database.seasonsQueries
                    .showSeasons(Id(showId))
                    .executeAsList()
                    .filter { it.season_number > 0 || (it.season_number == 0L && it.season_title != "Specials") }

                seasons.flatMap { season ->
                    database.seasonsQueries
                        .seasonDetails(showId = Id(showId), seasonNumber = season.season_number)
                        .executeAsList()
                        .mapNotNull { episodeDetail ->
                            episodeDetail.episode_id?.let { episodeId ->
                                // Check if this episode is watched
                                val isWatched = watchedEpisodes.any { it.episode_id.id == episodeId.id }
                                if (!isWatched) {
                                    (season.season_number * 1000L) + (episodeDetail.episode_number ?: 0L)
                                } else {
                                    null
                                }
                            }
                        }
                }
            }

            val earliestUnwatchedAbs = allEpisodes.minOrNull() ?: return@withContext false
            return@withContext earliestUnwatchedAbs < lastWatched.absoluteEpisodeNumber()
        }
    }

    override suspend fun findEarliestUnwatchedEpisode(showId: Long): NextEpisodeWithShow? {
        return withContext(dispatchers.databaseRead) {
            val allEpisodes = database.transactionWithResult {
                val seasons = database.seasonsQueries
                    .showSeasons(Id(showId))
                    .executeAsList()
                    .filter { it.season_number > 0 || (it.season_number == 0L && it.season_title != "Specials") }

                seasons.flatMap { season ->
                    database.seasonsQueries
                        .seasonDetails(showId = Id(showId), seasonNumber = season.season_number)
                        .executeAsList()
                        .mapNotNull { episodeDetail ->
                            episodeDetail.episode_id?.let { episodeId ->
                                // Check if this episode is watched
                                val isWatched = database.watchedEpisodesQueries
                                    .getWatchedEpisodes(Id(showId))
                                    .executeAsList()
                                    .any { it.episode_id.id == episodeId.id }

                                if (!isWatched) {
                                    // Convert to NextEpisodeWithShow format
                                    NextEpisodeWithShow(
                                        showId = showId,
                                        episodeId = episodeId.id,
                                        episodeName = episodeDetail.episode_title ?: "Episode ${episodeDetail.episode_number}",
                                        seasonNumber = season.season_number,
                                        episodeNumber = episodeDetail.episode_number ?: 0L,
                                        runtime = episodeDetail.runtime,
                                        stillPath = episodeDetail.episode_image_url,
                                        overview = episodeDetail.overview ?: "",
                                        showName = episodeDetail.show_title,
                                        showPoster = null,
                                    )
                                } else {
                                    null
                                }
                            }
                        }
                }
            }

            // Return the episode with the smallest absolute episode number
            allEpisodes.minByOrNull { (it.seasonNumber * 1000L) + it.episodeNumber }
        }
    }

    override suspend fun isWatchingOutOfOrder(showId: Long): Boolean {
        return withContext(dispatchers.databaseRead) {
            val watchedEpisodes = database.watchedEpisodesQueries
                .getWatchedEpisodes(Id(showId))
                .executeAsList()

            if (watchedEpisodes.size < 2) return@withContext false

            val sortedByEpisodeOrder = watchedEpisodes.sortedBy { it.absoluteEpisodeNumber() }
            val sortedByWatchTime = watchedEpisodes.sortedBy { it.watched_at }

            // Compare if the order of watching matches the episode order
            sortedByEpisodeOrder.zip(sortedByWatchTime).any { (episode, watched) ->
                episode.episode_id != watched.episode_id
            }
        }
    }

    override fun observeLastWatchedEpisode(showId: Long): Flow<LastWatchedEpisode?> {
        return database.showsLastWatchedQueries
            .lastWatchedEpisodeForShow(Id(showId))
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)
            .map { result ->
                result?.let {
                    LastWatchedEpisode(
                        showId = it.show_id.id,
                        episodeId = it.episode_id.id,
                        seasonNumber = it.last_watched_season.toInt(),
                        episodeNumber = it.last_watched_episode.toInt(),
                        watchedAt = it.last_watched_at ?: 0L,
                    )
                }
            }
    }
}
