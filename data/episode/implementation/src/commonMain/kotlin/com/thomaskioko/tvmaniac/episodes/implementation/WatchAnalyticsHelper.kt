package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgressContext
import com.thomaskioko.tvmaniac.episodes.api.model.absoluteEpisodeNumber
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

private const val EPISODES_PER_SEASON_LIMIT = 1000L

@Inject
@SingleIn(AppScope::class)
public class WatchAnalyticsHelper(
    private val database: TvManiacDatabase,
    private val datastoreRepository: DatastoreRepository,
    private val dispatchers: AppCoroutineDispatchers,
) {
    public suspend fun getWatchProgressContext(showId: Long): WatchProgressContext {
        return withContext(dispatchers.databaseRead) {
            val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
            val totalEpisodes = database.episodesQueries
                .countEpisodesForShow(
                    showId = Id(showId),
                    includeSpecials = if (includeSpecials) 1L else 0L,
                )
                .executeAsOne()
                .toInt()

            val watchedEpisodesCount = database.watchedEpisodesQueries
                .getWatchedEpisodes(Id(showId))
                .executeAsList()
                .size

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
                isWatchingOutOfOrder = isOutOfOrder,
                hasUnwatchedEarlierEpisodes = hasEarlierUnwatched,
                progressPercentage = progressPercentage,
            )
        }
    }

    public suspend fun hasUnwatchedEarlierEpisodes(showId: Long): Boolean {
        return withContext(dispatchers.databaseRead) {
            val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
            val watchedEpisodes = database.watchedEpisodesQueries
                .getWatchedEpisodes(Id(showId))
                .executeAsList()

            val lastWatched = watchedEpisodes.maxByOrNull { it.absoluteEpisodeNumber() }
                ?: return@withContext false

            // Use a Set for O(1) lookup
            val watchedEpisodeIds = watchedEpisodes.map { it.episode_id.id }.toSet()

            val allEpisodes = database.transactionWithResult {
                val seasons = database.seasonsQueries
                    .showSeasons(
                        showId = Id(showId),
                        includeSpecials = if (includeSpecials) 1L else 0L,
                    )
                    .executeAsList()

                seasons.flatMap { season ->
                    database.seasonsQueries
                        .seasonDetails(showId = Id(showId), seasonNumber = season.season_number)
                        .executeAsList()
                        .mapNotNull { episodeDetail ->
                            episodeDetail.episode_id?.let { episodeId ->
                                // Efficient O(1) check for unwatched episodes
                                if (!watchedEpisodeIds.contains(episodeId.id)) {
                                    (season.season_number * EPISODES_PER_SEASON_LIMIT) + (episodeDetail.episode_number ?: 0L)
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

    public suspend fun isWatchingOutOfOrder(showId: Long): Boolean {
        return withContext(dispatchers.databaseRead) {
            val watchedEpisodes = database.watchedEpisodesQueries
                .getWatchedEpisodes(Id(showId))
                .executeAsList()

            if (watchedEpisodes.size < 2) return@withContext false

            val watchedInOrder = watchedEpisodes
                .sortedBy { it.watched_at }
                .map { it.absoluteEpisodeNumber() }

            // If the watched order is not strictly increasing, it's out of order
            for (i in 1 until watchedInOrder.size) {
                if (watchedInOrder[i] < watchedInOrder[i - 1]) {
                    return@withContext true
                }
            }
            false
        }
    }
}
