package com.thomaskioko.tvmaniac.resourcemanager.api

import kotlin.time.Duration

interface RequestManagerRepository {

    fun upsert(lastRequests: LastRequest): Long
    fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean
    fun delete(id: Long)
    fun deleteAll()
}
