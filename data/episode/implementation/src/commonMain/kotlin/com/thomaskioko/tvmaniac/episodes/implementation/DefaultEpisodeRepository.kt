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
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markAsWatched(
            showTraktId = showTraktId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            watchedAt = timestamp,
            includeSpecials = includeSpecials,
        )
        syncRepository.value.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markEpisodeAndPreviousAsWatched(
            showTraktId = showTraktId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            timestamp = timestamp,
            includeSpecials = includeSpecials,
        )
        syncRepository.value.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markEpisodeAsUnwatched(showTraktId: Long, episodeId: Long) {
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markAsUnwatched(showTraktId, episodeId, includeSpecials)
        syncRepository.value.syncShowEpisodeWatches(showTraktId)
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
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        val episodes = watchedEpisodeDao.getEpisodesForSeason(showTraktId, seasonNumber)
        watchedEpisodeDao.markSeasonAsWatched(
            showTraktId = showTraktId,
            seasonNumber = seasonNumber,
            episodes = episodes,
            timestamp = timestamp,
            includeSpecials = includeSpecials,
        )
        syncRepository.value.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markSeasonAndPreviousSeasonsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        watchedAt: Instant?,
    ) {
        val timestamp = watchedAt?.toEpochMilliseconds() ?: dateTimeProvider.nowMillis()
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markSeasonAndPreviousAsWatched(
            showTraktId = showTraktId,
            seasonNumber = seasonNumber,
            timestamp = timestamp,
            includeSpecials = includeSpecials,
        )
        syncRepository.value.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markSeasonUnwatched(showTraktId: Long, seasonNumber: Long) {
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        watchedEpisodeDao.markSeasonAsUnwatched(showTraktId, seasonNumber, includeSpecials)
        syncRepository.value.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun getUnwatchedCountAfterFetchingPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
    ): Long {
        val includeSpecials = datastoreRepository.observeIncludeSpecials().first()
        val seasons = seasonsRepository.observeSeasonsByShowId(showTraktId).first()
        val previousSeasons = seasons.filter { it.season_number in 1..<seasonNumber }
        previousSeasons.parallelForEach { season ->
            currentCoroutineContext().ensureActive()
            seasonDetailsRepository.fetchSeasonDetails(
                SeasonDetailsParam(
                    showTraktId = showTraktId,
                    seasonId = season.season_id.id,
                    seasonNumber = season.season_number,
                ),
            )
        }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeContinueTrackingEpisodes(
        showTraktId: Long,
    ): Flow<ContinueTrackingResult?> =
        watchlistDao.observeIsShowInLibrary(showTraktId)
            .flatMapLatest { isInLibrary ->
                if (!isInLibrary) return@flatMapLatest flowOf(null)
                observeTrackingResultForLibraryShow(showTraktId)
            }
            .catch { emit(null) }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTrackingResultForLibraryShow(
        showTraktId: Long,
    ): Flow<ContinueTrackingResult?> =
        combine(
            observeLastWatchedEpisode(showTraktId),
            watchedEpisodeDao.observeWatchedEpisodes(showTraktId),
            seasonsRepository.observeSeasonsByShowId(showTraktId),
        ) { lastWatched, _, seasons ->
            lastWatched to seasons
        }.flatMapLatest { (lastWatched, seasons) ->
            if (seasons.isEmpty()) return@flatMapLatest flowOf(null)
            val startIndex = determineActiveSeasonIndex(seasons, lastWatched)
            observeFirstSeasonWithUnwatchedEpisodes(
                showTraktId = showTraktId,
                seasons = seasons,
                currentIndex = startIndex,
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeFirstSeasonWithUnwatchedEpisodes(
        showTraktId: Long,
        seasons: List<ShowSeasons>,
        currentIndex: Int,
    ): Flow<ContinueTrackingResult?> {
        if (currentIndex >= seasons.size) return flowOf(null)

        val season = seasons[currentIndex]
        val param = SeasonDetailsParam(
            showTraktId = showTraktId,
            seasonId = season.season_id.id,
            seasonNumber = season.season_number,
        )

        return seasonDetailsRepository.observeSeasonDetails(param)
            .flatMapLatest { seasonDetails ->
                when {
                    seasonDetails == null -> flowOf(null)
                    seasonDetails.episodes.any { !it.isWatched } -> {
                        flowOf(
                            ContinueTrackingResult(
                                episodes = seasonDetails.episodes.toImmutableList(),
                                firstUnwatchedIndex = calculateScrollIndex(seasonDetails.episodes),
                                currentSeasonNumber = seasonDetails.seasonNumber,
                                currentSeasonId = seasonDetails.seasonId,
                            ),
                        )
                    }

                    else -> observeFirstSeasonWithUnwatchedEpisodes(
                        showTraktId = showTraktId,
                        seasons = seasons,
                        currentIndex = currentIndex + 1,
                    )
                }
            }
    }

    private fun determineActiveSeasonIndex(
        seasons: List<ShowSeasons>,
        lastWatched: LastWatchedEpisode?,
    ): Int {
        return lastWatched?.let { watched ->
            seasons.indexOfFirst { it.season_number == watched.seasonNumber }
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
