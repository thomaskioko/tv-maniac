package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.db.Last_requests
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultRequestManagerRepository(
  private val database: TvManiacDatabase,
) : RequestManagerRepository {

  override fun upsert(entityId: Long, requestType: String, timestamp: Instant): Long {
    database.lastRequestsQueries.upsert(
      entity_id = entityId,
      request_type = requestType,
      timestamp = timestamp,
    )
    return database.lastRequestsQueries.lastInsertRowId().executeAsOne()
  }

  override fun delete(entityId: Long, requestType: String) {
    database.lastRequestsQueries.delete(entityId, requestType)
  }

  override fun deleteAll() {
    database.lastRequestsQueries.deleteAll()
  }

  override fun isRequestExpired(entityId: Long, requestType: String, threshold: Duration): Boolean =
    isRequestBefore(entityId, requestType, Clock.System.now() - threshold)

  override fun isRequestValid(requestType: String, threshold: Duration): Boolean {
    return !isRequestExpired(requestType, threshold)
  }

  fun isRequestExpired(requestType: String, threshold: Duration): Boolean {
    return getLastRequest(requestType)?.timestamp?.let { it < Clock.System.now() - threshold } ?: true
  }


  private fun isRequestBefore(entityId: Long, requestType: String, instant: Instant): Boolean {
    return getLastRequest(requestType, entityId)?.timestamp?.let { it < instant } ?: true
  }

  private fun getLastRequest(requestType: String, entityId: Long): Last_requests? {
    return database.lastRequestsQueries
      .getLastRequestForId(requestType, entityId)
      .executeAsOneOrNull()
  }

  private fun getLastRequest(requestType: String): Last_requests? {
    return database.lastRequestsQueries
      .getLastRequestForType(requestType)
      .executeAsOneOrNull()
  }
}
