package com.thomaskioko.tvmaniac.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.showcommon.api.repository.TmdbRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncTmdbArtWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tmdbRepository: TmdbRepository
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "sync-tmdb-art-work"
    }

    override suspend fun doWork(): Result {
        Logger.d("$tags worker running")
        tmdbRepository.syncShowArtWork()

        return Result.success()
    }
}
