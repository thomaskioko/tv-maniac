package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.db.Last_requests
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultRequestManagerRepository(
    private val database: TvManiacDatabase,
    private val dateTimeProvider: DateTimeProvider,
) : RequestManagerRepository {

    override fun upsert(entityId: Long, requestType: String, timestamp: Instant) {
        val _ = database.lastRequestsQueries.upsert(
            entity_id = entityId,
            request_type = requestType,
            timestamp = timestamp,
        )
    }

    override fun delete(entityId: Long, requestType: String) {
        val _ = database.lastRequestsQueries.delete(entityId, requestType)
    }

    override fun deleteAll() {
        val _ = database.lastRequestsQueries.deleteAll()
    }

    override fun clearSyncRelatedRequests() {
        val syncTypes = listOf(
            RequestTypeConfig.LIBRARY_SYNC.name,
            RequestTypeConfig.EPISODE_WATCHES_SYNC.name,
            RequestTypeConfig.SHOW_EPISODE_WATCHES_SYNC.name,
            RequestTypeConfig.USER_PROFILE.name,
        )
        syncTypes.forEach { type ->
            database.lastRequestsQueries.deleteByType(type)
        }
    }

    override fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean =
        isRequestBefore(entityId, requestType, dateTimeProvider.now() - threshold)

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
