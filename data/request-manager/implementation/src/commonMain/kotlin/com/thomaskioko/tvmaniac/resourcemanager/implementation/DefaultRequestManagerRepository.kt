package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.db.Last_requests
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultRequestManagerRepository(
    private val database: TvManiacDatabase,
) : RequestManagerRepository {

    override fun upsert(entityId: Long, requestType: String, timestamp: Instant): Long {
        database.lastRequestsQueries.upsert(
            entity_id = entityId,
            request_type = requestType,
            timestamp = timestamp,
        )
        return database.lastRequestsQueries.lastInsertRowId().executeAsOne()
    }

    override fun delete(entityId: Long, requestType: String) {
        database.lastRequestsQueries.delete(entityId, requestType)
    }

    override fun deleteAll() {
        database.lastRequestsQueries.deleteAll()
    }

    override fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean =
        isRequestBefore(entityId, requestType, Clock.System.now() - threshold)

    override fun isRequestValid(requestType: String, threshold: Duration): Boolean {
        val requestTypeConfig = RequestTypeConfig.valueOf(requestType)
        return !isRequestExpired(requestTypeConfig.requestId, requestType, threshold)
    }

    private fun isRequestBefore(entityId: Long, requestType: String, instant: Instant): Boolean {
        return getLastRequest(requestType, entityId)?.timestamp?.let { it < instant } ?: true
    }

    private fun getLastRequest(requestType: String, entityId: Long): Last_requests? {
        return database.lastRequestsQueries
            .getLastRequestForId(requestType, entityId)
            .executeAsOneOrNull()
    }
}
