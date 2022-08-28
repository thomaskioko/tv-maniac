package com.thomaskioko.tvmaniac.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncFollowedShows @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val traktRepository: TraktRepository
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "sync-followed-shows"
    }

    override suspend fun doWork(): Result {
        Logger.d("$tags worker running")
        traktRepository.syncFollowedShows()

        return Result.success()
    }
}
