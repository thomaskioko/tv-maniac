package com.thomaskioko.tvmaniac.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class SyncDiscoverShowsWorker(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val showsRepository: ShowsRepository,
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val DAILY_SYNC_TAG = "sync-discover-shows"
    }

    override suspend fun doWork(): Result {
        Logger.d("$tags worker running")
        showsRepository.fetchShows()
        return Result.success()
    }
}
