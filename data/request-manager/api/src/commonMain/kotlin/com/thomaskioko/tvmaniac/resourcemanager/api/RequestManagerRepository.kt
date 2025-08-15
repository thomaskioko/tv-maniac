package com.thomaskioko.tvmaniac.resourcemanager.api

import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Duration

interface RequestManagerRepository {

    fun upsert(entityId: Long, requestType: String, timestamp: Instant = Clock.System.now()): Long

    fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean

    fun isRequestValid(requestType: String, threshold: Duration): Boolean

    fun delete(entityId: Long, requestType: String)

    fun deleteAll()
}
