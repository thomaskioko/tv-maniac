package com.thomaskioko.tvmaniac.syncactivity.implementation

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedAccountRepository
import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultActivitySyncRepository(
    private val database: TvManiacDatabase,
    private val activityDao: TraktActivityDao,
    private val connectedAccountRepository: ConnectedAccountRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
) : ActivitySyncRepository {

    override suspend fun isAheadOf(consumerId: String, activityType: ActivityType): Boolean =
        withContext(dispatchers.databaseRead) {
            val provider = connectedAccountRepository.getActiveProvider() ?: return@withContext false
            val remote = activityDao.getByActivityType(activityType)?.remoteTimestamp
                ?: return@withContext false
            val synced = readCheckpoint(provider, consumerId, activityType)
            synced == null || remote > synced
        }

    override suspend fun markSyncedTo(consumerId: String, activityType: ActivityType) {
        withContext(dispatchers.databaseWrite) {
            val provider = connectedAccountRepository.getActiveProvider() ?: return@withContext
            val remote = activityDao.getByActivityType(activityType)?.remoteTimestamp ?: return@withContext
            database.activitySyncQueries.upsert(
                provider = provider.toDbProvider(),
                consumer_id = consumerId,
                activity_type = activityType.value,
                synced_until = remote,
                updated_at = dateTimeProvider.now(),
            )
        }
    }

    override suspend fun getSyncTimestamp(consumerId: String, activityType: ActivityType): Instant? =
        withContext(dispatchers.databaseRead) {
            val provider = connectedAccountRepository.getActiveProvider() ?: return@withContext null
            readCheckpoint(provider, consumerId, activityType)
        }

    override suspend fun clearAll() {
        withContext(dispatchers.databaseWrite) {
            database.activitySyncQueries.deleteAll()
        }
    }

    private fun readCheckpoint(provider: ConnectedProvider, consumerId: String, activityType: ActivityType): Instant? =
        database.activitySyncQueries
            .getCheckpoint(
                provider = provider.toDbProvider(),
                consumer_id = consumerId,
                activity_type = activityType.value,
            )
            .executeAsOneOrNull()
            ?.synced_until
}

private fun ConnectedProvider.toDbProvider(): Provider = when (this) {
    ConnectedProvider.TRAKT -> Provider.TRAKT
    ConnectedProvider.SIMKL -> Provider.SIMKL
}
