package com.thomaskioko.tvmaniac.nextepisode.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.model.WatchProgress
import com.thomaskioko.tvmaniac.nextepisode.api.model.absoluteEpisodeNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
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
    ) {
        withContext(dispatchers.databaseWrite) {
            database.transaction {
                database.watchedEpisodesQueries.markAsWatched(
                    show_id = Id(showId),
                    episode_id = Id(episodeId),
                    season_number = seasonNumber,
                    episode_number = episodeNumber,
                    watched_at = Clock.System.now().toEpochMilliseconds(),
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
}
