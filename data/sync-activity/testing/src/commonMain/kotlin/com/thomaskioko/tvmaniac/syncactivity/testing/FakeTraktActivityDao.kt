package com.thomaskioko.tvmaniac.syncactivity.testing

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.api.model.TraktLastActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

public class FakeTraktActivityDao(
    private val database: TvManiacDatabase,
) : TraktActivityDao {

    private val queries get() = database.traktLastActivityQueries

    override fun upsert(activityType: ActivityType, remoteTimestamp: Instant, fetchedAt: Instant) {
        val _ = queries.upsert(
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
        val _ = queries.markAsSynced(activityType.value)
    }

    override fun observeAll(): Flow<List<TraktLastActivity>> {
        return queries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
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
        val _ = queries.delete(activityType.value)
    }

    override fun deleteAll() {
        val _ = queries.deleteAll()
    }
}
