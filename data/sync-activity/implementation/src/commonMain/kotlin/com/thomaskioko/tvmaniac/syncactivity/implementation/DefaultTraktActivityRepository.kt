package com.thomaskioko.tvmaniac.syncactivity.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktActivityRepository(
    private val store: TraktActivityStore,
    private val activityDao: TraktActivityDao,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktActivityRepository {

    override suspend fun fetchLatestActivities(forceRefresh: Boolean) {
        when {
            forceRefresh -> store.fresh(Unit)
            else -> store.get(Unit)
        }
    }

    override suspend fun clearAllActivities() {
        withContext(dispatchers.io) {
            activityDao.deleteAll()
        }
    }
}
