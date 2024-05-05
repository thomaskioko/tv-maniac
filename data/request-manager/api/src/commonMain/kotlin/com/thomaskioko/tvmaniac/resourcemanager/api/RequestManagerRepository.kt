package com.thomaskioko.tvmaniac.resourcemanager.api

import kotlin.time.Duration

interface RequestManagerRepository {

  fun upsert(entityId: Long, requestType: String)

  fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean

  fun delete(id: Long)

  fun deleteAll()
}
