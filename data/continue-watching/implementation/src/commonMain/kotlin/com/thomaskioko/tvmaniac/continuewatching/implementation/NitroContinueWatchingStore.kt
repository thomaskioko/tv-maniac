package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
@SingleIn(AppScope::class)
public class NitroContinueWatchingStore(
    private val nitroFetcher: NitroContinueWatchingFetcher,
    private val continueWatchingDao: ContinueWatchingDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) {

    private val store: Store<Unit, List<ContinueWatchingEntry>> = storeBuilder(
        fetcher = Fetcher.ofResult { _: Unit ->
            when (val entries = nitroFetcher()) {
                null -> FetcherResult.Error.Exception(
                    FetcherSkipSignal("Nitro fetcher signaled skip; leaving local table unchanged"),
                )
                else -> FetcherResult.Data(entries)
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { _: Unit -> continueWatchingDao.entriesObservable() },
            writer = { _: Unit, entries: List<ContinueWatchingEntry> ->
                val incomingTraktIds = entries.map { it.traktId }.toSet()
                transactionRunner {
                    continueWatchingDao.entries()
                        .filter { it.traktId !in incomingTraktIds }
                        .forEach { continueWatchingDao.deleteByTraktId(it.traktId) }
                    entries.forEach { continueWatchingDao.upsert(it) }
                    entries.forEach { entry -> entry.toMinimalTvshow()?.let(tvShowsDao::upsertMerging) }
                }
                requestManagerRepository.upsert(
                    entityId = CONTINUE_WATCHING_SYNC.requestId,
                    requestType = CONTINUE_WATCHING_SYNC.name,
                )
                traktActivityRepository.markActivityAsSynced(ActivityType.EPISODES_WATCHED)
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

    public suspend fun fetchWith(forceRefresh: Boolean) {
        if (forceRefresh) store.fresh(Unit) else store.get(Unit)
    }
}

private fun ContinueWatchingEntry.toMinimalTvshow(): Tvshow? {
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
