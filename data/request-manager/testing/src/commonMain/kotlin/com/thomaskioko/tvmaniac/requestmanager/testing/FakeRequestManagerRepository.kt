package com.thomaskioko.tvmaniac.requestmanager.testing

import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import kotlin.time.Duration
import kotlin.time.Instant

public class FakeRequestManagerRepository : RequestManagerRepository {
    public var requestValid: Boolean = true
    public var upsertCalled: Boolean = false

    override fun upsert(entityId: Long, requestType: String, timestamp: Instant) {
        upsertCalled = true
    }

    override fun isRequestExpired(
        entityId: Long,
        requestType: String,
        threshold: Duration,
    ): Boolean = !requestValid

    override fun isRequestValid(requestType: String, threshold: Duration): Boolean = requestValid

    override fun delete(entityId: Long, requestType: String) {
        // No-op for testing
    }

    override fun deleteAll() {
    }

    override fun clearSyncRelatedRequests() {
    }
}
