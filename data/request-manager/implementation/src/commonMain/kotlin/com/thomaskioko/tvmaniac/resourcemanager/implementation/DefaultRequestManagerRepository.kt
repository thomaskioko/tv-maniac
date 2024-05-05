package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.core.db.Last_requests
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultRequestManagerRepository(
  private val database: TvManiacDatabase,
) : RequestManagerRepository {

  override fun upsert(entityId: Long, requestType: String) {
    database.last_requestsQueries.upsert(
      entity_id = entityId,
      request_type = requestType,
      timestamp = Clock.System.now(),
    )
  }

  override fun delete(id: Long) {
    database.last_requestsQueries.delete(id)
  }

  override fun deleteAll() {
    database.last_requestsQueries.deleteAll()
  }

  override fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean =
    isRequestBefore(entityId, requestType, Clock.System.now() - threshold)

  private fun isRequestBefore(entityId: Long, requestType: String, instant: Instant): Boolean {
    return getLastRequest(requestType, entityId)?.timestamp?.let { it < instant } ?: true
  }

  private fun getLastRequest(requestType: String, entityId: Long): Last_requests? {
    return database.last_requestsQueries
      .getLastRequestForId(requestType, entityId)
      .executeAsList()
      .firstOrNull()
  }
}
