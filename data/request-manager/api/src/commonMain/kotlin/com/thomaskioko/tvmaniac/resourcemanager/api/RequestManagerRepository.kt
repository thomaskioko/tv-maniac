package com.thomaskioko.tvmaniac.resourcemanager.api

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

public interface RequestManagerRepository {

    public fun upsert(entityId: Long, requestType: String, timestamp: Instant = Clock.System.now())

    public fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean

    public fun isRequestValid(requestType: String, threshold: Duration): Boolean

    public fun delete(entityId: Long, requestType: String)

    public fun deleteAll()
}
