package com.thomaskioko.tvmaniac.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.util.KermitLogger
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SyncWatchlist(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val logger: KermitLogger,
    private val repository: WatchlistRepository,
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "sync-watchlist-shows"
    }

    override suspend fun doWork(): Result {
        logger.debug("$tags worker running")
        repository.syncWatchlist()

        return Result.success()
    }
}
