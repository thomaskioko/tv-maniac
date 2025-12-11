package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.LastWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UnwatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgressContext
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withTimeoutOrNull
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultEpisodeRepository(
    private val watchedEpisodeDao: WatchedEpisodeDao,
    private val nextEpisodeDao: DefaultNextEpisodeDao,
    private val database: TvManiacDatabase,
    private val datastoreRepository: DatastoreRepository,
    private val dispatchers: AppCoroutineDispatchers,
    private val seasonsRepository: SeasonsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val watchlistDao: WatchlistDao,
    private val watchAnalyticsHelper: WatchAnalyticsHelper,
    private val dateTimeProvider: DateTimeProvider,
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
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        watchedEpisodeDao.markAsWatched(showId, episodeId, seasonNumber, episodeNumber, timestamp)
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        watchedEpisodeDao.markEpisodeAndPreviousAsWatched(
            showId = showId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            timestamp = timestamp,
        )
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

    override suspend fun isEpisodeWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Boolean =
        watchedEpisodeDao.isEpisodeWatched(showId, seasonNumber, episodeNumber)

    override suspend fun clearCachedWatchHistoryForShow(showId: Long) {
        watchedEpisodeDao.deleteAllForShow(showId)
    }

    override suspend fun getWatchProgressContext(showId: Long): WatchProgressContext =
        watchAnalyticsHelper.getWatchProgressContext(showId)

    override suspend fun hasUnwatchedEarlierEpisodes(showId: Long): Boolean =
        watchAnalyticsHelper.hasUnwatchedEarlierEpisodes(showId)

    override suspend fun findEarliestUnwatchedEpisode(showId: Long): NextEpisodeWithShow? {
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        return watchedEpisodeDao.getEarliestUnwatchedEpisode(showId, includeSpecials)
    }

    override suspend fun isWatchingOutOfOrder(showId: Long): Boolean =
        watchAnalyticsHelper.isWatchingOutOfOrder(showId)

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
                        watchedAt = it.last_watched_at,
                    )
                }
            }
            .distinctUntilChanged()
    }

    override fun observeSeasonWatchProgress(
        showId: Long,
        seasonNumber: Long,
    ): Flow<SeasonWatchProgress> =
        watchedEpisodeDao.observeSeasonWatchProgress(showId, seasonNumber)
            .distinctUntilChanged()

    override fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress> =
        watchedEpisodeDao.observeShowWatchProgress(showId)
            .distinctUntilChanged()

    override suspend fun markSeasonWatched(showId: Long, seasonNumber: Long, watchedAt: Instant?) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        val episodes = watchedEpisodeDao.getEpisodesForSeason(showId, seasonNumber)
        watchedEpisodeDao.markSeasonAsWatched(showId, seasonNumber, episodes, timestamp)
    }

    override suspend fun markSeasonAndPreviousSeasonsWatched(
        showId: Long,
        seasonNumber: Long,
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        watchedEpisodeDao.markSeasonAndPreviousAsWatched(showId, seasonNumber, timestamp)
    }

    override suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long) {
        watchedEpisodeDao.markSeasonAsUnwatched(showId, seasonNumber)
    }

    override suspend fun getUnwatchedEpisodesBefore(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): List<UnwatchedEpisode> =
        watchedEpisodeDao.getUnwatchedEpisodesBefore(showId, seasonNumber, episodeNumber)

    override suspend fun markMultipleEpisodesWatched(
        showId: Long,
        episodes: List<EpisodeWatchParams>,
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        watchedEpisodeDao.markMultipleAsWatched(showId, episodes, timestamp)
    }

    override suspend fun getUnwatchedEpisodesInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): List<UnwatchedEpisode> =
        watchedEpisodeDao.getUnwatchedEpisodesInPreviousSeasons(showId, seasonNumber)

    override suspend fun getUnwatchedEpisodeCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Long =
        watchedEpisodeDao.getUnwatchedEpisodeCountInPreviousSeasons(showId, seasonNumber)

    override suspend fun getUnwatchedCountAfterFetchingPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Long {
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        val seasons = seasonsRepository.observeSeasonsByShowId(showId, includeSpecials).first()
        val startSeasonNumber = if (includeSpecials) 0L else 1L
        val previousSeasons = seasons.filter {
            it.season_number in startSeasonNumber..<seasonNumber
        }
        previousSeasons.forEach { season ->
            seasonDetailsRepository.fetchSeasonDetails(
                SeasonDetailsParam(
                    showId = showId,
                    seasonId = season.season_id.id,
                    seasonNumber = season.season_number,
                ),
            )
        }
        return watchedEpisodeDao.getUnwatchedEpisodeCountInPreviousSeasons(showId, seasonNumber)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeContinueTrackingEpisodes(showId: Long): Flow<ContinueTrackingResult?> {
        return combine(
            watchlistDao.observeIsShowInLibrary(showId),
            datastoreRepository.observeIncludeSpecials(),
            observeLastWatchedEpisode(showId),
        ) { isInLibrary, includeSpecials, lastWatched ->
            Triple(isInLibrary, includeSpecials, lastWatched)
        }.mapLatest { (isInLibrary, includeSpecials, lastWatched) ->
            if (!isInLibrary) return@mapLatest null

            val seasons = seasonsRepository.observeSeasonsByShowId(showId, includeSpecials).first()
            if (seasons.isEmpty()) return@mapLatest null

            findFirstSeasonWithUnwatchedEpisodes(
                showId = showId,
                seasons = seasons,
                startIndex = determineActiveSeasonIndex(seasons, lastWatched),
            )
        }
    }

    private suspend fun findFirstSeasonWithUnwatchedEpisodes(
        showId: Long,
        seasons: List<ShowSeasons>,
        startIndex: Int,
    ): ContinueTrackingResult? {
        for (i in seasons.indices) {
            val index = (startIndex + i) % seasons.size
            val season = seasons[index]
            val param = SeasonDetailsParam(
                showId = showId,
                seasonId = season.season_id.id,
                seasonNumber = season.season_number,
            )

            val seasonDetails = withTimeoutOrNull(SEASON_FETCH_TIMEOUT_MS) {
                seasonDetailsRepository.fetchSeasonDetails(param)
                seasonDetailsRepository.observeSeasonDetails(param).first()
            }

            if (seasonDetails != null) {
                val episodes = seasonDetails.episodes.toImmutableList()
                val hasUnwatchedEpisodes = episodes.any { !it.isWatched }
                if (hasUnwatchedEpisodes) {
                    return ContinueTrackingResult(
                        episodes = episodes,
                        firstUnwatchedIndex = calculateScrollIndex(seasonDetails.episodes),
                        currentSeasonNumber = seasonDetails.seasonNumber,
                        currentSeasonId = seasonDetails.seasonId,
                    )
                }
            }
        }
        return null
    }

    private fun determineActiveSeasonIndex(
        seasons: List<ShowSeasons>,
        lastWatched: LastWatchedEpisode?,
    ): Int {
        return lastWatched?.let { watched ->
            seasons.indexOfFirst { it.season_number == watched.seasonNumber.toLong() }
                .takeIf { it >= 0 }
        } ?: 0
    }

    private fun calculateScrollIndex(episodes: List<EpisodeDetails>): Int {
        val firstUnwatched = episodes.indexOfFirst { !it.isWatched }
        if (firstUnwatched >= 0) return firstUnwatched

        val nextAfterLastWatched = episodes.indexOfLast { it.isWatched } + 1
        return if (nextAfterLastWatched < episodes.size) nextAfterLastWatched else 0
    }

    private companion object {
        const val SEASON_FETCH_TIMEOUT_MS = 30_000L
    }
}
