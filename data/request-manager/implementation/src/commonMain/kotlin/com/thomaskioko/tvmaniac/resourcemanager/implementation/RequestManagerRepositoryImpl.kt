package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.core.db.Last_requests
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration

@Inject
class RequestManagerRepositoryImpl constructor(
    private val database: TvManiacDatabase,
) : RequestManagerRepository {

    override fun insert(lastRequests: LastRequest): Long {
        database.last_requestsQueries.insert(
            id = lastRequests.id,
            entity_id = lastRequests.entityId,
            timestamp = lastRequests.timestamp,
            request_type = lastRequests.requestType,
        )
        return database.last_requestsQueries.lastInsertRowId().executeAsOne()
    }

    override fun update(entity: LastRequest) {
        val request = getLastRequest(entity.requestType, entity.entityId)

        when {
            request != null ->
                database.last_requestsQueries.update(
                    entity_id = entity.entityId,
                    request_type = entity.requestType,
                    timestamp = entity.timestamp,
                )

            else -> insert(entity)
        }
    }

    override fun delete(id: Long) {
        database.last_requestsQueries.delete(id)
    }

    override fun deleteAll() {
        database.last_requestsQueries.deleteAll()
    }

    override fun isRequestExpired(
        entityId: Long,
        requestType: String,
        threshold: Duration,
    ): Boolean = isRequestBefore(entityId, requestType, Clock.System.now() - threshold)

    private fun isRequestBefore(entityId: Long, requestType: String, instant: Instant): Boolean {
        return getLastRequest(requestType, entityId)?.timestamp?.let { it < instant } ?: true
    }

    private fun getLastRequest(requestType: String, entityId: Long): Last_requests? {
        return database.last_requestsQueries.getLastRequestForId(requestType, entityId)
            .executeAsList()
            .firstOrNull()
    }
}
