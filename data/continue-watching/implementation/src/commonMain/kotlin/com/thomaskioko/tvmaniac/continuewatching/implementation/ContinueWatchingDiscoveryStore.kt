package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.CONTINUE_WATCHING_SYNC
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

/**
 * Discovery half of the Progress continue-watching pipeline.
 *
 * Fetches `playback`, `hidden`, and `watched-shows` concurrently, then writes
 * skeleton rows for every non-hidden candidate so the watchlist surfaces the
 * cards with title and year immediately on a user-initiated refresh. Hidden
 * shows are removed from the DAO so the per-show fan-out in
 * [ContinueWatchingStore] does not re-surface them.
 *
 * Placeholder writes use `INSERT OR IGNORE`, so existing rows keep their real
 * counts. Authoritative counts land via [ContinueWatchingStore]'s SoT writer
 * once the per-show fan-out completes.
 */
@Inject
@SingleIn(AppScope::class)
public class ContinueWatchingDiscoveryStore(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val traktUserDataSource: TraktUserRemoteDataSource,
    private val continueWatchingDao: ContinueWatchingDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) {

    private val store: Store<Unit, List<ContinueWatchingDescriptor>> = storeBuilder(
        fetcher = Fetcher.ofResult { _: Unit ->
            coroutineScope {
                val playbackDeferred = async { traktSyncDataSource.getPlaybackEpisodes() }
                val hiddenDeferred = async { traktUserDataSource.getHiddenProgressWatched() }
                val watchedShowsDeferred = async {
                    traktSyncDataSource.getWatchedShows(page = 1, limit = WATCHED_SHOWS_LIMIT)
                }

                val playbackResponse = playbackDeferred.await()
                val watchedShowsResponse = watchedShowsDeferred.await()
                val hiddenResponse = hiddenDeferred.await()

                if (playbackResponse !is ApiResponse.Success ||
                    watchedShowsResponse !is ApiResponse.Success ||
                    hiddenResponse !is ApiResponse.Success
                ) {
                    FetcherResult.Error.Exception(
                        FetcherSkipSignal("Discovery upstream failed; leaving local table unchanged"),
                    )
                } else {
                    val hiddenIds = hiddenResponse.body
                        .mapNotNull { it.show?.ids?.trakt }
                        .toSet()
                    val descriptors = buildDescriptors(watchedShowsResponse.body, playbackResponse.body)
                        .filter { it.traktId !in hiddenIds }
                    FetcherResult.Data(DiscoveryResult(descriptors, hiddenIds))
                }
            }
        },
        sourceOfTruth = SourceOfTruth.of<Unit, DiscoveryResult, List<ContinueWatchingDescriptor>>(
            reader = { _: Unit ->
                continueWatchingDao.entriesObservable().map { rows ->
                    rows.map { row ->
                        ContinueWatchingDescriptor(
                            traktId = row.traktId,
                            tmdbId = row.tmdbId,
                            title = row.title,
                            year = row.year,
                        )
                    }
                }
            },
            writer = { _: Unit, discovery: DiscoveryResult ->
                transactionRunner {
                    discovery.hiddenIds.forEach(continueWatchingDao::deleteByTraktId)
                    discovery.descriptors.forEach { descriptor ->
                        continueWatchingDao.upsertPlaceholder(
                            traktId = descriptor.traktId,
                            tmdbId = descriptor.tmdbId,
                            title = descriptor.title,
                            year = descriptor.year,
                        )
                        descriptor.toMinimalTvshow()?.let(tvShowsDao::upsertMerging)
                    }
                }
            },
            delete = { _: Unit -> continueWatchingDao.deleteAll() },
            deleteAll = { continueWatchingDao.deleteAll() },
        ).usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
    ).validator(
        Validator.by {
            withContext(dispatchers.io) {
                val ttlValid = requestManagerRepository.isRequestValid(
                    requestType = CONTINUE_WATCHING_SYNC.name,
                    threshold = CONTINUE_WATCHING_SYNC.duration,
                )
                val activityChanged = traktActivityRepository.hasActivityChanged(ActivityType.EPISODES_WATCHED)
                ttlValid && !activityChanged
            }
        },
    ).build()

    /**
     * Triggers discovery. Throws [FetcherSkipSignal] (via Store5's error
     * propagation) when any upstream call fails. The Repository catches it so
     * the detail sync stays skipped on the same cycle.
     */
    public suspend fun fetchWith(forceRefresh: Boolean) {
        if (forceRefresh) {
            store.fresh(Unit)
        } else {
            store.get(Unit)
        }
    }

    private fun buildDescriptors(
        watchedShows: List<TraktWatchedShowResponse>,
        playback: List<TraktPlaybackEpisodeResponse>,
    ): List<ContinueWatchingDescriptor> = buildMap {
        watchedShows.forEach { item ->
            val show = item.show
            put(
                show.ids.trakt,
                ContinueWatchingDescriptor(
                    traktId = show.ids.trakt,
                    tmdbId = show.ids.tmdb,
                    title = show.title,
                    year = show.year,
                ),
            )
        }
        playback.forEach { item ->
            val show = item.show
            put(
                show.ids.trakt,
                ContinueWatchingDescriptor(
                    traktId = show.ids.trakt,
                    tmdbId = show.ids.tmdb,
                    title = show.title,
                    year = show.year,
                ),
            )
        }
    }.values.toList()

    private companion object {
        const val WATCHED_SHOWS_LIMIT = 100
    }
}

public data class ContinueWatchingDescriptor(
    val traktId: Long,
    val tmdbId: Long?,
    val title: String?,
    val year: Long?,
)

internal data class DiscoveryResult(
    val descriptors: List<ContinueWatchingDescriptor>,
    val hiddenIds: Set<Long>,
)

private fun ContinueWatchingDescriptor.toMinimalTvshow(): Tvshow? {
    val tmdb = tmdbId ?: return null
    val name = title ?: return null
    return Tvshow(
        trakt_id = Id<TraktId>(traktId),
        tmdb_id = Id<TmdbId>(tmdb),
        name = name,
        overview = "",
        language = null,
        year = year?.toString(),
        ratings = 0.0,
        vote_count = 0,
        genres = null,
        status = null,
        episode_numbers = null,
        season_numbers = null,
        poster_path = null,
        backdrop_path = null,
    )
}
