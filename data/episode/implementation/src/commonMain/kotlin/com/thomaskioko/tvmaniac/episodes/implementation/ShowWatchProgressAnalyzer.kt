package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgressContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class ShowWatchProgressAnalyzer(
    private val database: TvManiacDatabase,
    private val datastoreRepository: DatastoreRepository,
    private val dispatchers: AppCoroutineDispatchers,
) {
    private suspend fun getIncludeSpecials(): Boolean =
        datastoreRepository.observeIncludeSpecials().first()

    private fun checkUnwatchedEarlierEpisodes(
        showId: Long,
        includeSpecials: Boolean,
        lastWatchedAbsoluteNumber: Long,
    ): Boolean {
        return database.watchedEpisodesQueries
            .hasUnwatchedEarlierEpisodes(
                show_id = Id(showId),
                include_specials = if (includeSpecials) 1L else 0L,
                last_watched_absolute_number = lastWatchedAbsoluteNumber,
            )
            .executeAsOne()
    }

    private fun checkWatchingOutOfOrder(
        showId: Long,
        includeSpecials: Boolean,
    ): Boolean {
        val watchedInOrder = database.watchedEpisodesQueries
            .getWatchedEpisodesWithAbsoluteNumbers(
                show_id = Id(showId),
                include_specials = if (includeSpecials) 1L else 0L,
            )
            .executeAsList()

        if (watchedInOrder.size < 2) return false

        for (i in 1 until watchedInOrder.size) {
            if (watchedInOrder[i].absolute_number < watchedInOrder[i - 1].absolute_number) {
                return true
            }
        }
        return false
    }

    public suspend fun getWatchProgressContext(showId: Long): WatchProgressContext {
        return withContext(dispatchers.databaseRead) {
            val includeSpecials = getIncludeSpecials()

            val totalEpisodes = database.episodesQueries
                .countEpisodesForShow(
                    showId = Id(showId),
                    includeSpecials = if (includeSpecials) 1L else 0L,
                )
                .executeAsOne()
                .toInt()

            val watchedEpisodesCount = database.watchedEpisodesQueries
                .getWatchedEpisodesCountForShow(Id(showId))
                .executeAsOne()
                .toInt()

            val lastWatched = database.watchedEpisodesQueries
                .getLastWatchedWithAbsoluteNumber(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                .executeAsOneOrNull()

            val progressPercentage = if (totalEpisodes > 0) {
                (watchedEpisodesCount.toFloat() / totalEpisodes.toFloat()) * 100f
            } else {
                0f
            }

            val isOutOfOrder = checkWatchingOutOfOrder(showId, includeSpecials)
            val hasEarlierUnwatched = if (lastWatched != null) {
                checkUnwatchedEarlierEpisodes(
                    showId = showId,
                    includeSpecials = includeSpecials,
                    lastWatchedAbsoluteNumber = lastWatched.absolute_number,
                )
            } else {
                false
            }

            WatchProgressContext(
                showId = showId,
                totalEpisodes = totalEpisodes,
                watchedEpisodes = watchedEpisodesCount,
                lastWatchedSeasonNumber = lastWatched?.season_number?.toInt(),
                lastWatchedEpisodeNumber = lastWatched?.episode_number?.toInt(),
                isWatchingOutOfOrder = isOutOfOrder,
                hasUnwatchedEarlierEpisodes = hasEarlierUnwatched,
                progressPercentage = progressPercentage,
            )
        }
    }

    public suspend fun hasUnwatchedEarlierEpisodes(showId: Long): Boolean {
        return withContext(dispatchers.databaseRead) {
            val includeSpecials = getIncludeSpecials()

            val lastWatched = database.watchedEpisodesQueries
                .getLastWatchedWithAbsoluteNumber(
                    show_id = Id(showId),
                    include_specials = if (includeSpecials) 1L else 0L,
                )
                .executeAsOneOrNull() ?: return@withContext false

            checkUnwatchedEarlierEpisodes(
                showId = showId,
                includeSpecials = includeSpecials,
                lastWatchedAbsoluteNumber = lastWatched.absolute_number,
            )
        }
    }

    public suspend fun isWatchingOutOfOrder(showId: Long): Boolean {
        return withContext(dispatchers.databaseRead) {
            val includeSpecials = getIncludeSpecials()
            checkWatchingOutOfOrder(showId, includeSpecials)
        }
    }
}
