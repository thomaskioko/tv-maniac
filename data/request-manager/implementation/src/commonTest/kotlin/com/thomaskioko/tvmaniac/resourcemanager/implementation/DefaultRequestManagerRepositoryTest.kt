package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.LastRequestsQueries
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class DefaultRequestManagerRepositoryTest : BaseDatabaseTest() {
    private val lastRequestsQueries: LastRequestsQueries = database.lastRequestsQueries
    private val fakeDateTimeProvider = FakeDateTimeProvider()
    private lateinit var repository: DefaultRequestManagerRepository

    @BeforeTest
    fun setup() {
        repository = DefaultRequestManagerRepository(database, fakeDateTimeProvider)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should return last insert row id when upsert is called`() {
        val result = repository.upsert(1L, "TEST", fakeDateTimeProvider.now())
        val expectedRowId = lastRequestsQueries.lastInsertRowId().executeAsOne()

        result shouldBe expectedRowId
    }

    @Test
    fun `should update existing record for same entityId and requestType`() {
        val entityId = 1L
        val requestType = "TEST"
        val initialTimestamp = fakeDateTimeProvider.now() - 1.hours
        val updatedTimestamp = fakeDateTimeProvider.now()

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
        val updatedRequest = lastRequestsQueries.getLastRequestForId(requestType, entityId).executeAsOne()
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

        val request = lastRequestsQueries.getLastRequestForId(requestType, entityId).executeAsOneOrNull()

        request shouldBe null
    }

    @Test
    fun `should delete all records when deleteAll is called`() {
        val entityId = 1L
        val requestType = "TEST"

        repository.upsert(entityId, requestType)

        repository.deleteAll()

        val request = lastRequestsQueries.getLastRequestForId(requestType, entityId).executeAsOneOrNull()

        request shouldBe null
    }

    @Test
    fun `should return true when request is older than threshold`() {
        val entityId = 1L
        val requestType = "TEST"
        val threshold = 1.hours
        val oldTimestamp = fakeDateTimeProvider.now() - 2.hours

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
        val timestamp = fakeDateTimeProvider.now() - 30.minutes

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

    @Test
    fun `should return false when request is older than threshold`() {
        val requestType = RequestTypeConfig.TOP_RATED_SHOWS.name
        val entityId = RequestTypeConfig.TOP_RATED_SHOWS.requestId
        val threshold = 1.hours
        val oldTimestamp = fakeDateTimeProvider.now() - 2.hours

        repository.upsert(entityId, requestType, oldTimestamp)

        val result = repository.isRequestValid(requestType, threshold)

        result shouldBe false
    }

    @Test
    fun `should return true when request is newer than threshold`() {
        val requestType = RequestTypeConfig.TOP_RATED_SHOWS.name
        val entityId = RequestTypeConfig.TOP_RATED_SHOWS.requestId
        val threshold = 1.hours
        val recentTimestamp = fakeDateTimeProvider.now() - 30.minutes

        repository.upsert(entityId, requestType, recentTimestamp)

        val result = repository.isRequestValid(requestType, threshold)

        result shouldBe true
    }

    @Test
    fun `should handle multiple rows with same requestType but different entityId`() {
        val requestType = RequestTypeConfig.TOP_RATED_SHOWS.name
        val correctEntityId = RequestTypeConfig.TOP_RATED_SHOWS.requestId
        val differentEntityId = 999L
        val threshold = 1.hours

        val oldTimestamp = fakeDateTimeProvider.now() - 2.hours
        repository.upsert(correctEntityId, requestType, oldTimestamp)

        val recentTimestamp = fakeDateTimeProvider.now() - 30.minutes
        repository.upsert(differentEntityId, requestType, recentTimestamp)

        val result = repository.isRequestValid(requestType, threshold)

        result shouldBe false
    }

    @Test
    fun `should return false when no request exists for isRequestValid`() {
        val requestType = RequestTypeConfig.FEATURED_SHOWS_TODAY.name
        val threshold = 1.hours

        val result = repository.isRequestValid(requestType, threshold)

        result shouldBe false
    }

    @Test
    fun `should return false for isRequestExpired after updating an expired request`() {
        val entityId = 1L
        val requestType = "TEST"
        val threshold = 1.hours
        val oldTimestamp = fakeDateTimeProvider.now() - 2.hours

        repository.upsert(entityId, requestType, oldTimestamp)

        val initialResult = repository.isRequestExpired(entityId, requestType, threshold)
        initialResult shouldBe true

        val currentTimestamp = fakeDateTimeProvider.now()
        repository.upsert(entityId, requestType, currentTimestamp)

        val updatedResult = repository.isRequestExpired(entityId, requestType, threshold)
        updatedResult shouldBe false
    }
}
