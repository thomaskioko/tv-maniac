package com.thomaskioko.tvmaniac.nextepisode.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.GetNextEpisodeForShow
import com.thomaskioko.tvmaniac.db.GetNextEpisodesForWatchlist
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultNextEpisodeDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : NextEpisodeDao {

    override fun observeNextEpisode(showId: Long): Flow<NextEpisodeWithShow?> {
        return database.nextEpisodeCacheQueries
            .getNextEpisodeForShow(Id(showId))
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)
            .map { it?.toNextEpisodeWithShow() }
            .catch { emit(null) }
    }

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> {
        return database.nextEpisodeCacheQueries
            .getNextEpisodesForWatchlist()
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { episodes ->
                episodes.map { it.toNextEpisodeWithShow() }
            }
            .catch { emit(emptyList()) }
    }

    override suspend fun getNextEpisodeForShow(showId: Long): GetNextEpisodeForShow? {
        return withContext(dispatchers.databaseRead) {
            database.nextEpisodeCacheQueries
                .getNextEpisodeForShow(Id(showId))
                .executeAsOneOrNull()
        }
    }

    override fun upsert(
        showId: Long,
        episodeId: Long?,
        episodeName: String,
        episodeNumber: Long,
        seasonNumber: Long,
        airDate: String?,
        runtime: Int?,
        stillPath: String?,
        overview: String,
        isUpcoming: Boolean,
    ) {
        database.nextEpisodeCacheQueries.upsert(
            show_id = Id(showId),
            episode_id = episodeId?.let { Id(it) },
            episode_name = episodeName,
            episode_number = episodeNumber,
            season_number = seasonNumber,
            air_date = airDate,
            runtime = runtime?.toLong(),
            still_path = stillPath,
            overview = overview,
            is_upcoming = if (isUpcoming) 1L else 0L,
            updated_at = Clock.System.now().toEpochMilliseconds(),
        )
    }

    override suspend fun delete(showId: Long) {
        withContext(dispatchers.databaseWrite) {
            database.nextEpisodeCacheQueries.deleteNextEpisode(Id(showId))
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchers.databaseWrite) {
            database.nextEpisodeCacheQueries.deleteAll()
        }
    }

    override suspend fun getNextEpisodesCount(): Long {
        return withContext(dispatchers.databaseRead) {
            database.nextEpisodeCacheQueries.getNextEpisodesCount().executeAsOne()
        }
    }

    override suspend fun getStaleNextEpisodes(thresholdTimestamp: Long): List<Long> {
        return withContext(dispatchers.databaseRead) {
            database.nextEpisodeCacheQueries
                .getStaleNextEpisodes(thresholdTimestamp)
                .executeAsList()
                .map { it.id }
        }
    }

    private fun GetNextEpisodeForShow.toNextEpisodeWithShow(): NextEpisodeWithShow {
        return NextEpisodeWithShow(
            showId = show_id.id,
            showName = show_name,
            showPoster = show_poster,
            episodeId = episode_id?.id,
            episodeName = episode_name,
            seasonNumber = season_number,
            episodeNumber = episode_number,
            airDate = air_date,
            runtime = runtime,
            stillPath = still_path,
            overview = overview,
            isUpcoming = is_upcoming == 1L,
        )
    }

    private fun GetNextEpisodesForWatchlist.toNextEpisodeWithShow(): NextEpisodeWithShow {
        return NextEpisodeWithShow(
            showId = show_id.id,
            showName = show_name,
            showPoster = show_poster,
            episodeId = episode_id?.id,
            episodeName = episode_name,
            seasonNumber = season_number,
            episodeNumber = episode_number,
            airDate = air_date,
            runtime = runtime,
            stillPath = still_path,
            overview = overview,
            isUpcoming = is_upcoming == 1L,
            followedAt = followed_at,
        )
    }
}
