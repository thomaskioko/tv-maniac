package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.episodes.api.model.LastWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultEpisodeRepository(
    private val watchedEpisodeDao: WatchedEpisodeDao,
    private val nextEpisodeDao: DefaultNextEpisodeDao,
    private val episodesDao: EpisodesDao,
    private val database: TvManiacDatabase,
    private val datastoreRepository: DatastoreRepository,
    private val dispatchers: AppCoroutineDispatchers,
    private val syncRepository: WatchedEpisodeSyncRepository,
    private val upcomingEpisodesStore: UpcomingEpisodesStore,
) : EpisodeRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        datastoreRepository.observeIncludeSpecials()
            .flatMapLatest { includeSpecials ->
                nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials)
            }

    override suspend fun markEpisodeAsWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markAsWatched(
            showTraktId = showTraktId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markEpisodeAndPreviousAsWatched(
            showTraktId = showTraktId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markEpisodeAsUnwatched(showTraktId: Long, episodeId: Long) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markAsUnwatched(
            showTraktId = showTraktId,
            episodeId = episodeId,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override fun observeLastWatchedEpisode(showTraktId: Long): Flow<LastWatchedEpisode?> {
        return database.showsLastWatchedQueries
            .lastWatchedEpisodeForShow(Id(showTraktId))
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)
            .map { result ->
                result?.let {
                    LastWatchedEpisode(
                        showTraktId = it.show_trakt_id.id,
                        episodeId = it.episode_id.id,
                        seasonNumber = it.last_watched_season,
                        episodeNumber = it.last_watched_episode,
                    )
                }
            }
            .distinctUntilChanged()
    }

    override fun observeSeasonWatchProgress(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<SeasonWatchProgress> =
        watchedEpisodeDao.observeSeasonWatchProgress(showTraktId, seasonNumber)
            .distinctUntilChanged()

    override fun observeShowWatchProgress(showTraktId: Long): Flow<ShowWatchProgress> =
        watchedEpisodeDao.observeShowWatchProgress(showTraktId)
            .distinctUntilChanged()

    override fun observeAllSeasonsWatchProgress(showTraktId: Long): Flow<List<SeasonWatchProgress>> =
        watchedEpisodeDao.observeAllSeasonsWatchProgress(showTraktId)
            .distinctUntilChanged()

    override suspend fun markSeasonWatched(
        showTraktId: Long,
        seasonNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        val episodes = watchedEpisodeDao.getEpisodesForSeason(showTraktId, seasonNumber)
        watchedEpisodeDao.markSeasonAsWatched(
            showTraktId = showTraktId,
            seasonNumber = seasonNumber,
            episodes = episodes,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markSeasonAndPreviousSeasonsWatched(
        showTraktId: Long,
        seasonNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markSeasonAndPreviousAsWatched(
            showTraktId = showTraktId,
            seasonNumber = seasonNumber,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markSeasonUnwatched(showTraktId: Long, seasonNumber: Long) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markSeasonAsUnwatched(showTraktId, seasonNumber, includeSpecials)
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun getUnwatchedCountInPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
    ): Long {
        val includeSpecials = getIncludeSpecials()
        return watchedEpisodeDao.getUnwatchedEpisodeCountInPreviousSeasons(
            showTraktId = showTraktId,
            seasonNumber = seasonNumber,
            includeSpecials = includeSpecials,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUnwatchedCountInPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<Long> = datastoreRepository.observeIncludeSpecials()
        .flatMapLatest { includeSpecials ->
            watchedEpisodeDao.observeUnwatchedCountInPreviousSeasons(
                showTraktId,
                seasonNumber,
                includeSpecials,
            )
        }

    override suspend fun getUpcomingEpisodesFromFollowedShows(
        limit: Duration,
    ): List<UpcomingEpisode> =
        withContext(dispatchers.io) {
            episodesDao.getUpcomingEpisodesFromFollowedShows(limit)
                .map { episode ->
                    UpcomingEpisode(
                        episodeId = episode.episode_id.id,
                        seasonId = episode.season_id.id,
                        showId = episode.show_trakt_id.id,
                        episodeNumber = episode.episode_number,
                        seasonNumber = episode.season_number,
                        title = episode.title,
                        overview = episode.overview,
                        runtime = episode.runtime,
                        imageUrl = episode.image_url,
                        firstAired = episode.first_aired,
                        showName = episode.show_name,
                        showPoster = episode.show_poster,
                    )
                }
        }

    override suspend fun syncUpcomingEpisodesFromTrakt(
        startDate: String,
        days: Int,
        forceRefresh: Boolean,
    ) {
        val params = UpcomingEpisodesParams(startDate, days)
        when {
            forceRefresh -> upcomingEpisodesStore.fresh(params)
            else -> upcomingEpisodesStore.get(params)
        }
    }

    private suspend fun getIncludeSpecials(): Boolean = datastoreRepository.getIncludeSpecials()
}
