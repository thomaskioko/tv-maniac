package com.thomaskioko.tvmaniac.episodes.implementation.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.NextEpisodesForWatchlist
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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

    override fun observeNextEpisodesForWatchlist(includeSpecials: Boolean): Flow<List<NextEpisodeWithShow>> {
        return database.showsNextToWatchQueries
            .nextEpisodesForWatchlist(includeSpecials = if (includeSpecials) 1L else 0L)
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { list ->
                list.filter { it.episode_id != null }
                    .map { it.toNextEpisodeWithShow() }
            }
            .catch { emit(emptyList()) }
    }
}

private fun NextEpisodesForWatchlist.toNextEpisodeWithShow(): NextEpisodeWithShow {
    return NextEpisodeWithShow(
        showTraktId = show_trakt_id.id,
        showTmdbId = show_tmdb_id.id,
        episodeId = episode_id!!.id,
        episodeName = episode_name,
        seasonId = season_id!!.id,
        seasonNumber = season_number!!,
        episodeNumber = episode_number!!,
        runtime = runtime,
        stillPath = still_path,
        overview = overview,
        showName = show_name,
        showPoster = show_poster,
        showStatus = show_status,
        showYear = show_year,
        followedAt = followed_at,
        firstAired = first_aired,
        lastWatchedAt = last_watched_at,
        seasonCount = season_count,
        episodeCount = episode_count,
        watchedCount = watched_count,
        totalCount = total_count,
    )
}
