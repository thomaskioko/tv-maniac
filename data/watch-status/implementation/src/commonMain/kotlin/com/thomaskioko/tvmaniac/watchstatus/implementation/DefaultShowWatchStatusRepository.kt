package com.thomaskioko.tvmaniac.watchstatus.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchProgress
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusDao
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultShowWatchStatusRepository(
    private val dao: ShowWatchStatusDao,
    private val showIdResolver: ShowIdResolver,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowWatchStatusRepository {

    override suspend fun refresh(showId: Long) {
        withContext(dispatchers.databaseWrite) {
            val internalShowId = showIdResolver.showIdForTraktId(showId) ?: return@withContext
            val progress = dao.getWatchProgress(internalShowId) ?: return@withContext
            dao.upsert(
                showId = internalShowId,
                status = progress.toWatchStatus(),
                lastWatchedAt = null,
                lastSyncedAt = dateTimeProvider.nowMillis(),
            )
        }
    }
}

private fun ShowWatchProgress.toWatchStatus(): WatchStatus = when {
    totalCount in 1..watchedCount -> WatchStatus.COMPLETED
    watchedCount > 0 -> WatchStatus.WATCHING
    else -> WatchStatus.WATCHLIST
}
