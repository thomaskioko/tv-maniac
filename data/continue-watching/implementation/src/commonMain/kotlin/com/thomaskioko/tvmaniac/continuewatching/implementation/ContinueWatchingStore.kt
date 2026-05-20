package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.CONTINUE_WATCHING_SYNC
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
@SingleIn(AppScope::class)
public class ContinueWatchingStore(
    @Progress private val progressFetcher: ContinueWatchingFetcher,
    @Nitro private val nitroFetcher: ContinueWatchingFetcher,
    private val continueWatchingDao: ContinueWatchingDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) {

    private val store: Store<ContinueWatchingKey, List<ContinueWatchingEntry>> = storeBuilder(
        fetcher = Fetcher.ofResult { key: ContinueWatchingKey ->
            val fetcher: ContinueWatchingFetcher = when (key) {
                ContinueWatchingKey.Progress -> progressFetcher
                ContinueWatchingKey.Nitro -> nitroFetcher
            }
            val forceRefresh = currentCoroutineContext()[FetchHints]?.forceRefresh ?: false
            when (val entries = fetcher.run(forceRefresh)) {
                null -> FetcherResult.Error.Exception(
                    FetcherSkipSignal("Fetcher signaled skip; leaving local table unchanged"),
                )
                else -> FetcherResult.Data(entries)
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { _: ContinueWatchingKey -> continueWatchingDao.entriesObservable() },
            writer = { _: ContinueWatchingKey, entries: List<ContinueWatchingEntry> ->
                val incomingTraktIds = entries.map { it.traktId }.toSet()
                transactionRunner {
                    val existing = continueWatchingDao.entries()
                    existing
                        .filter { it.traktId !in incomingTraktIds }
                        .forEach { continueWatchingDao.deleteByTraktId(it.traktId) }
                    entries.forEach { continueWatchingDao.upsert(it) }
                }
                requestManagerRepository.upsert(
                    entityId = CONTINUE_WATCHING_SYNC.requestId,
                    requestType = CONTINUE_WATCHING_SYNC.name,
                )
                traktActivityRepository.markActivityAsSynced(ActivityType.EPISODES_WATCHED)
            },
            delete = { _: ContinueWatchingKey -> continueWatchingDao.deleteAll() },
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
     * Triggers a Store5 fetch or cache read depending on [forceRefresh]. The
     * [forceRefresh] flag is plumbed to the fetcher lambda via [FetchHints] on
     * the coroutine context so the key can stay free of call-time state.
     *
     * Throws [FetcherSkipSignal] (via Store5's error propagation) when a
     * fetcher returns null. Callers should catch it explicitly if they want
     * "skip the write" to be silent.
     */
    public suspend fun fetchWith(key: ContinueWatchingKey, forceRefresh: Boolean) {
        withContext(FetchHints(forceRefresh)) {
            if (forceRefresh) {
                store.fresh(key)
            } else {
                store.get(key)
            }
        }
    }
}
