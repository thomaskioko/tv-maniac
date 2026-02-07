package com.thomaskioko.tvmaniac.upnext.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.NextEpisodeWithShowInfoByShowId
import com.thomaskioko.tvmaniac.db.NextEpisodesWithShowInfo
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.upnext.api.UpNextDao
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
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
public class DefaultUpNextDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
    private val dateTimeProvider: DateTimeProvider,
) : UpNextDao {

    override fun observeNextEpisodesFromCache(): Flow<List<NextEpisodeWithShow>> {
        return database.nextEpisodesQueries
            .nextEpisodesWithShowInfo()
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { it.filterActionableEpisodes(dateTimeProvider.nowMillis()) }
    }

    override suspend fun getNextEpisodesFromCache(): List<NextEpisodeWithShow> {
        return withContext(dispatchers.databaseRead) {
            database.nextEpisodesQueries
                .nextEpisodesWithShowInfo()
                .executeAsList()
                .filterActionableEpisodes(dateTimeProvider.nowMillis())
        }
    }

    override fun observeNextEpisodeForShow(showTraktId: Long): Flow<List<NextEpisodeWithShow>> {
        return database.nextEpisodesQueries
            .nextEpisodeWithShowInfoByShowId(Id(showTraktId))
            .asFlow()
            .mapToList(dispatchers.databaseRead)
            .map { list -> list.mapNotNull { it.toNextEpisodeWithShow() } }
    }

    override suspend fun upsert(
        showTraktId: Long,
        episodeTraktId: Long?,
        seasonNumber: Long?,
        episodeNumber: Long?,
        title: String?,
        overview: String?,
        runtime: Long?,
        firstAired: Long?,
        imageUrl: String?,
        isShowComplete: Boolean,
        lastEpisodeSeason: Long?,
        lastEpisodeNumber: Long?,
        traktLastWatchedAt: Long?,
        updatedAt: Long,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.nextEpisodesQueries.upsert(
                show_trakt_id = Id(showTraktId),
                episode_trakt_id = episodeTraktId,
                season_number = seasonNumber,
                episode_number = episodeNumber,
                title = title,
                overview = overview,
                runtime = runtime,
                first_aired = firstAired,
                image_url = imageUrl,
                is_show_complete = if (isShowComplete) 1L else 0L,
                last_episode_season = lastEpisodeSeason,
                last_episode_number = lastEpisodeNumber,
                trakt_last_watched_at = traktLastWatchedAt,
                updated_at = updatedAt,
            )
        }
    }

    override suspend fun upsertShowProgress(
        showTraktId: Long,
        watchedCount: Long,
        totalCount: Long,
    ) {
        withContext(dispatchers.databaseWrite) {
            database.showMetadataQueries.upsertWithProgress(
                show_trakt_id = Id(showTraktId),
                cached_watched_count = watchedCount,
                cached_total_count = totalCount,
            )
        }
    }

    override suspend fun advanceAfterWatched(
        showTraktId: Long,
        watchedSeason: Long,
        watchedEpisode: Long,
    ) {
        val now = dateTimeProvider.nowMillis()
        withContext(dispatchers.databaseWrite) {
            database.nextEpisodesQueries.advanceAfterWatched(
                show_trakt_id = Id(showTraktId),
                watched_season = watchedSeason,
                watched_episode = watchedEpisode,
                watched_at = now,
                updated_at = now,
            )
        }
    }

    override suspend fun deleteForShow(showTraktId: Long) {
        withContext(dispatchers.databaseWrite) {
            database.nextEpisodesQueries.deleteForShow(Id(showTraktId))
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchers.databaseWrite) {
            database.nextEpisodesQueries.deleteAll()
        }
    }
}

private fun NextEpisodeWithShowInfoByShowId.toNextEpisodeWithShow(): NextEpisodeWithShow? {
    val episodeId = episode_id ?: return null
    val seasonId = season_id ?: return null
    val seasonNumber = season_number ?: return null
    val episodeNumber = episode_number ?: return null
    return NextEpisodeWithShow(
        showTraktId = show_trakt_id.id,
        showTmdbId = show_tmdb_id.id,
        episodeId = episodeId,
        episodeName = episode_name,
        seasonId = seasonId.id,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
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

private fun List<NextEpisodesWithShowInfo>.filterActionableEpisodes(
    nowMillis: Long,
): List<NextEpisodeWithShow> = mapNotNull { episode ->
    val airDate = episode.first_aired
    val isCaughtUp = episode.total_count > 0 && episode.watched_count >= episode.total_count
    val hasNotAired = airDate == null || airDate > nowMillis
    if (isCaughtUp && hasNotAired) return@mapNotNull null
    episode.toNextEpisodeWithShow()
}

private fun NextEpisodesWithShowInfo.toNextEpisodeWithShow(): NextEpisodeWithShow? {
    val episodeId = episode_id ?: return null
    val seasonId = season_id ?: return null
    val seasonNumber = season_number ?: return null
    val episodeNumber = episode_number ?: return null
    return NextEpisodeWithShow(
        showTraktId = show_trakt_id.id,
        showTmdbId = show_tmdb_id.id,
        episodeId = episodeId,
        episodeName = episode_name,
        seasonId = seasonId.id,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
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
