package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.LastRequestsQueries
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.Clock

class DefaultRequestManagerRepositoryTest : BaseDatabaseTest() {
  private val lastRequestsQueries: LastRequestsQueries = database.lastRequestsQueries
  private lateinit var repository: DefaultRequestManagerRepository

  @BeforeTest
  fun setup() {
    repository = DefaultRequestManagerRepository(database)
  }

  @AfterTest
  fun tearDown() {
    closeDb()
  }

  @Test
  fun `should return last insert row id when upsert is called`() {

    val result = repository.upsert(1L, "TEST", Clock.System.now())
    val expectedRowId = lastRequestsQueries.lastInsertRowId().executeAsOne()

    result shouldBe expectedRowId
  }

  @Test
  fun `should update existing record for same entityId and requestType`() {
    val entityId = 1L
    val requestType = "TEST"
    val initialTimestamp = Clock.System.now() - 1.hours
    val updatedTimestamp = Clock.System.now()

    // Perform the initial insert
    repository.upsert(entityId, requestType, initialTimestamp)

    // Check the initial state
    val initialRequest = lastRequestsQueries.getLastRequestForId(requestType, entityId).executeAsOne()
    initialRequest.entity_id shouldBe entityId
    initialRequest.request_type shouldBe requestType
    initialRequest.timestamp.toEpochMilliseconds() shouldBe initialTimestamp.toEpochMilliseconds()

    // Count the number of rows
    val initialCount = lastRequestsQueries.countRows().executeAsOne()

    // Perform the upsert with updated timestamp
    repository.upsert(entityId, requestType, updatedTimestamp)

    // Check the updated state
    val updatedRequest =
      lastRequestsQueries.getLastRequestForId(requestType, entityId).executeAsOne()
    updatedRequest.entity_id shouldBe entityId
    updatedRequest.request_type shouldBe requestType
    updatedRequest.timestamp.toEpochMilliseconds() shouldBe updatedTimestamp.toEpochMilliseconds()

    // Verify that no new row was created
    val finalCount = lastRequestsQueries.countRows().executeAsOne()
    finalCount shouldBe initialCount
  }

  @Test
  fun `should delete existing record for same entityId and requestType`() {
    val entityId = 1L
    val requestType = "TEST"

    repository.upsert(entityId, requestType)

    repository.delete(entityId, requestType)

    val request =
      lastRequestsQueries.getLastRequestForId(requestType, entityId).executeAsOneOrNull()

    request shouldBe null
  }

  @Test
  fun `should delete all records when deleteAll is called`() {
    val entityId = 1L
    val requestType = "TEST"

    repository.upsert(entityId, requestType)

    repository.deleteAll()

    val request =
      lastRequestsQueries.getLastRequestForId(requestType, entityId).executeAsOneOrNull()

    request shouldBe null
  }

  @Test
  fun `should return true when request is older than threshold`() {
    val entityId = 1L
    val requestType = "TEST"
    val threshold = 1.hours
    val oldTimestamp = Clock.System.now() - 2.hours

    repository.upsert(entityId, requestType, oldTimestamp)

    lastRequestsQueries.getLastRequestForId(requestType, entityId)

    val result = repository.isRequestExpired(entityId, requestType, threshold)

    result shouldBe true
  }

  @Test
  fun `should return false when request is newer than threshold`() {
    val entityId = 1L
    val requestType = "TEST"
    val threshold = 1.hours
    val timestamp = Clock.System.now() - 30.minutes

    repository.upsert(entityId, requestType, timestamp)

    val result = repository.isRequestExpired(entityId, requestType, threshold)

    result shouldBe false
  }

  @Test
  fun `should return true when no request exists`() {
    val entityId = 1L
    val requestType = "TEST"
    val threshold = 1.hours

    val result = repository.isRequestExpired(entityId, requestType, threshold)

    result shouldBe true
  }
}
