package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.NextEpisodeForShow
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

    override fun observeNextEpisode(showId: Long): Flow<NextEpisodeWithShow?> {
        return database.showsNextToWatchQueries
            .nextEpisodeForShow(Id(showId))
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)
            .map { it?.toNextEpisodeWithShow() }
            .catch { emit(null) }
    }

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> {
        return database.showsNextToWatchQueries
            .nextEpisodesForWatchlist()
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { list ->
                // Group by show and take the first (earliest) episode per show
                list.groupBy { it.show_id }
                    .map { (_, episodes) ->
                        episodes.minByOrNull { it.season_number * 1000 + it.episode_number }!!
                    }
                    .map { it.toNextEpisodeWithShow() }
            }
            .catch { emit(emptyList()) }
    }
}

private fun NextEpisodeForShow.toNextEpisodeWithShow(): NextEpisodeWithShow {
    return NextEpisodeWithShow(
        showId = show_id.id,
        episodeId = episode_id.id,
        episodeName = episode_name,
        seasonNumber = season_number,
        episodeNumber = episode_number,
        runtime = runtime,
        stillPath = still_path,
        overview = overview,
        showName = show_name,
        showPoster = show_poster,
    )
}

private fun NextEpisodesForWatchlist.toNextEpisodeWithShow(): NextEpisodeWithShow {
    return NextEpisodeWithShow(
        showId = show_id.id,
        episodeId = episode_id.id,
        episodeName = episode_name,
        seasonNumber = season_number,
        episodeNumber = episode_number,
        runtime = runtime,
        stillPath = still_path,
        overview = overview,
        showName = show_name,
        showPoster = show_poster,
        followedAt = followed_at,
    )
}
