package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.episodes.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.episodes.api.model.LastWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
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
    private val dateTimeProvider: DateTimeProvider,
    private val syncRepository: Lazy<WatchedEpisodeSyncRepository>,
) : EpisodeRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        datastoreRepository.observeIncludeSpecials()
            .flatMapLatest { includeSpecials ->
                nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials)
            }

    override suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markAsWatched(
            showId = showId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            watchedAt = timestamp,
            includeSpecials = includeSpecials,
        )
        syncRepository.value.syncShowEpisodeWatches(showId)
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markEpisodeAndPreviousAsWatched(
            showId = showId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            timestamp = timestamp,
            includeSpecials = includeSpecials,
        )
        syncRepository.value.syncShowEpisodeWatches(showId)
    }

    override suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long) {
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markAsUnwatched(showId, episodeId, includeSpecials)
    }

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
                        seasonNumber = it.last_watched_season,
                        episodeNumber = it.last_watched_episode,
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

    override fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>> =
        watchedEpisodeDao.observeAllSeasonsWatchProgress(showId)
            .distinctUntilChanged()

    override suspend fun markSeasonWatched(showId: Long, seasonNumber: Long, watchedAt: Instant?) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        val episodes = watchedEpisodeDao.getEpisodesForSeason(showId, seasonNumber)
        watchedEpisodeDao.markSeasonAsWatched(
            showId = showId,
            seasonNumber = seasonNumber,
            episodes = episodes,
            timestamp = timestamp,
            includeSpecials = includeSpecials,
        )
        syncRepository.value.syncShowEpisodeWatches(showId)
    }

    override suspend fun markSeasonAndPreviousSeasonsWatched(
        showId: Long,
        seasonNumber: Long,
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markSeasonAndPreviousAsWatched(
            showId = showId,
            seasonNumber = seasonNumber,
            timestamp = timestamp,
            includeSpecials = includeSpecials,
        )
        syncRepository.value.syncShowEpisodeWatches(showId)
    }

    override suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long) {
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markSeasonAsUnwatched(showId, seasonNumber, includeSpecials)
    }

    override suspend fun getUnwatchedCountAfterFetchingPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Long {
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        val seasons = seasonsRepository.observeSeasonsByShowId(showId).first()
        val previousSeasons = seasons.filter { it.season_number in 1..<seasonNumber }
        previousSeasons.parallelForEach { season ->
            currentCoroutineContext().ensureActive()
            seasonDetailsRepository.fetchSeasonDetails(
                SeasonDetailsParam(
                    showId = showId,
                    seasonId = season.season_id.id,
                    seasonNumber = season.season_number,
                ),
            )
        }
        return watchedEpisodeDao.getUnwatchedEpisodeCountInPreviousSeasons(
            showId = showId,
            seasonNumber = seasonNumber,
            includeSpecials = includeSpecials,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Flow<Long> = datastoreRepository.observeIncludeSpecials()
        .flatMapLatest { includeSpecials ->
            watchedEpisodeDao.observeUnwatchedCountInPreviousSeasons(showId, seasonNumber, includeSpecials)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeContinueTrackingEpisodes(showId: Long): Flow<ContinueTrackingResult?> =
        watchlistDao.observeIsShowInLibrary(showId)
            .flatMapLatest { isInLibrary ->
                if (!isInLibrary) return@flatMapLatest flowOf(null)
                observeTrackingResultForLibraryShow(showId)
            }
            .catch { emit(null) }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTrackingResultForLibraryShow(showId: Long): Flow<ContinueTrackingResult?> =
        combine(
            observeLastWatchedEpisode(showId),
            watchedEpisodeDao.observeWatchedEpisodes(showId),
            seasonsRepository.observeSeasonsByShowId(showId),
        ) { lastWatched, _, seasons ->
            lastWatched to seasons
        }.mapLatest { (lastWatched, seasons) ->
            seasons.takeIf { it.isNotEmpty() }?.let { availableSeasons ->
                val startIndex = determineActiveSeasonIndex(availableSeasons, lastWatched)
                findFirstSeasonWithUnwatchedEpisodes(showId, availableSeasons, startIndex)
            }
        }

    private suspend fun findFirstSeasonWithUnwatchedEpisodes(
        showId: Long,
        seasons: List<ShowSeasons>,
        startIndex: Int,
    ): ContinueTrackingResult? {
        for (i in startIndex until seasons.size) {
            val season = seasons[i]
            val param = SeasonDetailsParam(
                showId = showId,
                seasonId = season.season_id.id,
                seasonNumber = season.season_number,
            )

            val seasonDetails = seasonDetailsRepository.observeSeasonDetails(param).first()
                ?: continue

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
}
