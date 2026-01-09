package com.thomaskioko.tvmaniac.core.tasks.implementation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.followedshows.FollowedShowsSyncInteractor
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
public class LibrarySyncWorker(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val followedShowsSyncInteractor: Lazy<FollowedShowsSyncInteractor>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val dateTimeProvider: Lazy<DateTimeProvider>,
    private val logger: Logger,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        logger.debug(TAG, "Library sync worker starting")

        if (!traktAuthRepository.value.isLoggedIn()) {
            logger.debug(TAG, "User not logged in, skipping sync")
            return Result.success()
        }

        return try {
            followedShowsSyncInteractor.value.executeSync(
                FollowedShowsSyncInteractor.Param(forceRefresh = true),
            )
            datastoreRepository.value.setLastSyncTimestamp(dateTimeProvider.value.nowMillis())
            logger.debug(TAG, "Library sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            logger.error(TAG, "Library sync failed: ${e.message}")
            Result.failure()
        }
    }

    public companion object {
        public const val NAME: String = "library-sync-worker"
        private const val TAG = "LibrarySyncWorker"
    }
}
