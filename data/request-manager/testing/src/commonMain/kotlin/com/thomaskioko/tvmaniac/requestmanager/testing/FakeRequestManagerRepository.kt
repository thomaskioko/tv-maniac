package com.thomaskioko.tvmaniac.requestmanager.testing

import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.implementation.DefaultRequestManagerRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.time.Duration
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultRequestManagerRepository::class])
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
