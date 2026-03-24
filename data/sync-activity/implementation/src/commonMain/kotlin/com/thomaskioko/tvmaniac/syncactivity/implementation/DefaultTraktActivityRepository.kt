package com.thomaskioko.tvmaniac.syncactivity.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktActivityRepository(
    private val store: TraktActivityStore,
    private val activityDao: TraktActivityDao,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : TraktActivityRepository {

    override suspend fun fetchLatestActivities(forceRefresh: Boolean) {
        when {
            forceRefresh -> store.fresh(Unit) { logger.debug(TAG, it) }
            else -> store.get(Unit) { logger.debug(TAG, it) }
        }
    }

    override suspend fun hasActivityChanged(activityType: ActivityType): Boolean =
        withContext(dispatchers.io) {
            activityDao.isDurationExpired(activityType)
        }

    override suspend fun markActivityAsSynced(activityType: ActivityType) {
        withContext(dispatchers.io) {
            activityDao.markAsSynced(activityType)
            logger.debug(TAG, "Marked $activityType as synced")
        }
    }

    override suspend fun clearAllActivities() {
        withContext(dispatchers.io) {
            activityDao.deleteAll()
        }
    }

    private companion object {
        private const val TAG = "TraktActivityRepository"
    }
}
