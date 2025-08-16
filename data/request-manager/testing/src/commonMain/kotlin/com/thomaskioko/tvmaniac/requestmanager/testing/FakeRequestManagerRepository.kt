package com.thomaskioko.tvmaniac.requestmanager.testing

import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import kotlin.time.Duration
import kotlin.time.Instant

class FakeRequestManagerRepository : RequestManagerRepository {
    override fun upsert(entityId: Long, requestType: String, timestamp: Instant): Long = 0L

    override fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean = false

    override fun isRequestValid(requestType: String, threshold: Duration): Boolean = true

    override fun delete(entityId: Long, requestType: String) {
        // No-op for testing
    }

    override fun deleteAll() {
        // No-op for testing
    }
}
