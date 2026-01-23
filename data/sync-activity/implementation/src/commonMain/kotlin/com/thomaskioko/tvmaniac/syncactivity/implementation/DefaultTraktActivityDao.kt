package com.thomaskioko.tvmaniac.syncactivity.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.TraktLastActivityQueries
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.api.model.TraktLastActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktActivityDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktActivityDao {

    private val queries: TraktLastActivityQueries
        get() = database.traktLastActivityQueries

    override fun upsert(activityType: ActivityType, remoteTimestamp: Instant, fetchedAt: Instant) {
        queries.upsert(
            activity_type = activityType.value,
            remote_timestamp = remoteTimestamp,
            activity_type_ = activityType.value,
            fetched_at = fetchedAt,
        )
    }

    override fun getByActivityType(activityType: ActivityType): TraktLastActivity? {
        return queries.getByActivityType(activityType.value)
            .executeAsOneOrNull()
            ?.let { row ->
                TraktLastActivity(
                    id = row.id,
                    activityType = ActivityType.fromValue(row.activity_type) ?: activityType,
                    remoteTimestamp = row.remote_timestamp,
                    syncedRemoteTimestamp = row.synced_remote_timestamp,
                    fetchedAt = row.fetched_at,
                )
            }
    }

    override fun isDurationExpired(activityType: ActivityType): Boolean {
        val result = queries.hasActivityChanged(activityType.value).executeAsOneOrNull()
        return result == 1L
    }

    override fun markAsSynced(activityType: ActivityType) {
        queries.markAsSynced(activityType.value)
    }

    override fun observeAll(): Flow<List<TraktLastActivity>> {
        return queries.getAll()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.mapNotNull { row ->
                    ActivityType.fromValue(row.activity_type)?.let { type ->
                        TraktLastActivity(
                            id = row.id,
                            activityType = type,
                            remoteTimestamp = row.remote_timestamp,
                            syncedRemoteTimestamp = row.synced_remote_timestamp,
                            fetchedAt = row.fetched_at,
                        )
                    }
                }
            }
    }

    override fun getAll(): List<TraktLastActivity> {
        return queries.getAll()
            .executeAsList()
            .mapNotNull { row ->
                ActivityType.fromValue(row.activity_type)?.let { type ->
                    TraktLastActivity(
                        id = row.id,
                        activityType = type,
                        remoteTimestamp = row.remote_timestamp,
                        syncedRemoteTimestamp = row.synced_remote_timestamp,
                        fetchedAt = row.fetched_at,
                    )
                }
            }
    }

    override fun delete(activityType: ActivityType) {
        queries.delete(activityType.value)
    }

    override fun deleteAll() {
        queries.deleteAll()
    }
}
