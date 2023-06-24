package com.thomaskioko.tvmaniac.resourcemanager.api

import kotlin.time.Duration

interface RequestManagerRepository {

    fun insert(lastRequests: LastRequest): Long
    fun update(entity: LastRequest)
    fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean
    fun delete(id: Long)
    fun deleteAll()
}
