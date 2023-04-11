package com.thomaskioko.tvmaniac.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.trakt.profile.api.ProfileRepository
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SyncFollowedShows(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val profileRepository: ProfileRepository
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "sync-followed-shows"
    }

    override suspend fun doWork(): Result {
        Logger.d("$tags worker running")
        profileRepository.fetchTraktWatchlistShows()

        return Result.success()
    }
}
