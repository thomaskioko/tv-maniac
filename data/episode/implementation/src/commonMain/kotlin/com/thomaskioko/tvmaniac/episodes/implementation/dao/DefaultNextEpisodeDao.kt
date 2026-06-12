package com.thomaskioko.tvmaniac.episodes.implementation.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.CompletedShowsForWatchlist
import com.thomaskioko.tvmaniac.db.NextEpisodesForWatchlist
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.upnext.api.model.CompletedShow
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultNextEpisodeDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
    private val dateTimeProvider: DateTimeProvider,
) : NextEpisodeDao {

    override fun observeNextEpisodesForWatchlist(includeSpecials: Boolean): Flow<List<NextEpisodeWithShow>> {
        return database.showsNextToWatchQueries
            .nextEpisodesForWatchlist(includeSpecials = if (includeSpecials) 1L else 0L)
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { rows ->
                rows.map { it.toNextEpisodeWithShow() }
                    .filterActionableEpisodes(dateTimeProvider.nowMillis())
            }
            .catch { emit(emptyList()) }
    }

    override fun observeCompletedShows(): Flow<List<CompletedShow>> {
        return database.showsNextToWatchQueries
            .completedShowsForWatchlist()
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { rows -> rows.map { it.toCompletedShow() } }
            .catch { emit(emptyList()) }
    }
}

private fun CompletedShowsForWatchlist.toCompletedShow(): CompletedShow {
    return CompletedShow(
        showId = show_id.id,
        showTmdbId = tmdb_id.id,
        showName = show_name,
        showPoster = show_poster,
        lastWatchedAt = last_watched_at,
        watchedCount = watched_count,
        totalCount = total_count,
    )
}

private fun NextEpisodesForWatchlist.toNextEpisodeWithShow(): NextEpisodeWithShow {
    return NextEpisodeWithShow(
        showId = show_id.id,
        showTmdbId = tmdb_id.id,
        episodeId = episode_id?.id,
        episodeName = episode_name,
        seasonId = season_id?.id,
        seasonNumber = season_number,
        episodeNumber = episode_number,
        runtime = runtime,
        stillPath = still_path,
        overview = overview,
        showName = show_name,
        showPoster = show_poster,
        showStatus = show_status,
        showYear = show_year,
        firstAired = first_aired,
        lastWatchedAt = last_watched_at,
        seasonCount = season_count,
        episodeCount = episode_count,
        watchedCount = watched_count,
        totalCount = total_count,
        rating = ratings,
        voteCount = vote_count,
    )
}

private fun List<NextEpisodeWithShow>.filterActionableEpisodes(
    nowMillis: Long,
): List<NextEpisodeWithShow> = filter { episode ->
    val airDate = episode.firstAired
    val isCaughtUp = episode.totalCount > 0 && episode.watchedCount >= episode.totalCount
    val hasNotAired = airDate == null || airDate > nowMillis
    !(isCaughtUp && hasNotAired)
}
