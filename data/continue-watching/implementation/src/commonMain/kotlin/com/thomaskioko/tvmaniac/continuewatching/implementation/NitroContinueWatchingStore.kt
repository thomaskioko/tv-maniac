package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.CONTINUE_WATCHING_SYNC
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncTypes
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
public class NitroContinueWatchingStore(
    private val nitroFetcher: NitroContinueWatchingFetcher,
    private val continueWatchingDao: ContinueWatchingDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val syncRepository: ActivitySyncRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) {

    public suspend fun fetchWith(forceRefresh: Boolean) {
        if (!forceRefresh && isFresh()) return

        nitroFetcher().collect { batch ->
            if (batch == null) {
                throw FetcherSkipSignal("Nitro fetcher signaled skip; leaving local table unchanged")
            }
            apply(batch)
        }
    }

    private suspend fun isFresh(): Boolean = withContext(dispatchers.io) {
        val ttlValid = requestManagerRepository.isRequestValid(
            requestType = CONTINUE_WATCHING_SYNC.name,
            threshold = CONTINUE_WATCHING_SYNC.duration,
        )
        val watchedChanged = syncRepository.isAheadOf(
            consumerId = ActivitySyncTypes.NITRO_CONTINUE_WATCHING,
            activityType = ActivityType.EPISODES_WATCHED,
        )
        val pausedChanged = syncRepository.isAheadOf(
            consumerId = ActivitySyncTypes.NITRO_CONTINUE_WATCHING,
            activityType = ActivityType.EPISODES_PAUSED,
        )
        ttlValid && !watchedChanged && !pausedChanged
    }

    private suspend fun apply(batch: ProgressBatch) {
        when (batch) {
            is ProgressBatch.Entry -> withContext(dispatchers.databaseWrite) {
                transactionRunner {
                    batch.entry.toMinimalTvshow(batch.traktId)?.let(tvShowsDao::upsertMerging)
                    continueWatchingDao.upsert(batch.entry)
                }
            }
            is ProgressBatch.Complete -> {
                withContext(dispatchers.databaseWrite) {
                    transactionRunner {
                        continueWatchingDao.entries()
                            .filter { it.showId !in batch.finalTraktIds }
                            .forEach { continueWatchingDao.deleteByShowId(it.showId) }
                    }
                }
                requestManagerRepository.upsert(
                    entityId = CONTINUE_WATCHING_SYNC.requestId,
                    requestType = CONTINUE_WATCHING_SYNC.name,
                )
                syncRepository.markSyncedTo(
                    consumerId = ActivitySyncTypes.NITRO_CONTINUE_WATCHING,
                    activityType = ActivityType.EPISODES_WATCHED,
                )
                syncRepository.markSyncedTo(
                    consumerId = ActivitySyncTypes.NITRO_CONTINUE_WATCHING,
                    activityType = ActivityType.EPISODES_PAUSED,
                )
            }
        }
    }
}

private fun ContinueWatchingEntry.toMinimalTvshow(traktId: Long? = null): ShowToPersist? {
    val tmdb = tmdbId ?: return null
    val name = title ?: return null
    return ShowToPersist(
        showId = Id<TraktId>(traktId ?: showId),
        tmdbId = Id<TmdbId>(tmdb),
        name = name,
        overview = "",
        language = null,
        year = year?.toString(),
        ratings = 0.0,
        voteCount = 0,
        genres = null,
        status = null,
        episodeNumbers = null,
        seasonNumbers = null,
        posterPath = null,
        backdropPath = null,
    )
}
