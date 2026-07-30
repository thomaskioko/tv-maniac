package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

internal class CacheValidationTest : BaseDatabaseTest() {
    private lateinit var repository: DefaultRequestManagerRepository
    private val fakeDateTimeProvider = FakeDateTimeProvider(currentTime = FIXED_NOW)

    @BeforeTest
    fun setup() {
        repository = DefaultRequestManagerRepository(database, fakeDateTimeProvider)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `isRequestExpired should return true when no request exists`() {
        val result = repository.isRequestExpired(
            entityId = 123L,
            requestType = "SHOW_DETAILS",
            threshold = 1.days,
        )

        result shouldBe true
    }

    @Test
    fun `isRequestExpired should return true when request is older than threshold`() {
        val entityId = 123L
        val requestType = "SHOW_DETAILS"
        val twoDaysAgo = fakeDateTimeProvider.now() - 2.days

        repository.upsert(entityId, requestType, twoDaysAgo)

        val result = repository.isRequestExpired(
            entityId = entityId,
            requestType = requestType,
            threshold = 1.days,
        )

        result shouldBe true
    }

    @Test
    fun `isRequestExpired should return false when request is newer than threshold`() {
        val entityId = 123L
        val requestType = "SHOW_DETAILS"
        val thirtyMinutesAgo = fakeDateTimeProvider.now() - 30.minutes

        repository.upsert(entityId, requestType, thirtyMinutesAgo)

        val result = repository.isRequestExpired(
            entityId = entityId,
            requestType = requestType,
            threshold = 1.hours,
        )

        result shouldBe false
    }

    @Test
    fun `isRequestValid should return false when no request exists for global data`() {
        val result = repository.isRequestValid(
            requestType = "TRENDING_SHOWS_TODAY",
            threshold = 1.days,
        )

        result shouldBe false
    }

    @Test
    fun `isRequestValid should return false when global request is expired`() {
        val requestType = "TRENDING_SHOWS_TODAY"
        val requestConfig = RequestTypeConfig.valueOf(requestType)
        val twoDaysAgo = fakeDateTimeProvider.now() - 2.days

        repository.upsert(requestConfig.requestId, requestType, twoDaysAgo)

        val result = repository.isRequestValid(
            requestType = requestType,
            threshold = 1.days,
        )

        result shouldBe false
    }

    @Test
    fun `isRequestValid should return true when global request is valid`() {
        val requestType = "TRENDING_SHOWS_TODAY"
        val requestConfig = RequestTypeConfig.valueOf(requestType)
        val thirtyMinutesAgo = fakeDateTimeProvider.now() - 30.minutes

        repository.upsert(requestConfig.requestId, requestType, thirtyMinutesAgo)

        val result = repository.isRequestValid(
            requestType = requestType,
            threshold = 1.hours,
        )

        result shouldBe true
    }

    @Test
    fun `isRequestExpired should return false when request is exactly at the threshold`() {
        val entityId = 123L
        val requestType = "SHOW_DETAILS"
        val exactlyOneDayAgo = fakeDateTimeProvider.now() - 1.days

        repository.upsert(entityId, requestType, exactlyOneDayAgo)

        val result = repository.isRequestExpired(
            entityId = entityId,
            requestType = requestType,
            threshold = 1.days,
        )

        result shouldBe false
    }

    @Test
    fun `should handle multiple entities with same request type`() {
        val requestType = "SHOW_DETAILS"
        val now = fakeDateTimeProvider.now()

        repository.upsert(1L, requestType, now - 30.minutes)
        repository.upsert(2L, requestType, now - 2.days)
        repository.upsert(3L, requestType, now - 61.minutes)

        repository.isRequestExpired(1L, requestType, 1.hours) shouldBe false
        repository.isRequestExpired(2L, requestType, 1.hours) shouldBe true
        repository.isRequestExpired(3L, requestType, 1.hours) shouldBe true
    }

    @Test
    fun `should handle multiple request types for same entity`() {
        val entityId = 123L
        val now = fakeDateTimeProvider.now()

        repository.upsert(entityId, "SHOW_DETAILS", now - 30.minutes)
        repository.upsert(entityId, "SIMILAR_SHOWS", now - 2.hours)
        repository.upsert(entityId, "SEASON_DETAILS", now - 1.days)

        repository.isRequestExpired(entityId, "SHOW_DETAILS", 1.hours) shouldBe false
        repository.isRequestExpired(entityId, "SIMILAR_SHOWS", 1.hours) shouldBe true
        repository.isRequestExpired(entityId, "SEASON_DETAILS", 1.hours) shouldBe true
    }

    @Test
    fun `delete should remove specific entityId and requestType combination`() {
        val entityId = 123L
        val requestType = "SHOW_DETAILS"
        val now = fakeDateTimeProvider.now()

        repository.upsert(entityId, requestType, now)
        repository.upsert(entityId, "DIFFERENT_TYPE", now)
        repository.upsert(456L, requestType, now)

        repository.isRequestExpired(entityId, requestType, 1.hours) shouldBe false
        repository.isRequestExpired(entityId, "DIFFERENT_TYPE", 1.hours) shouldBe false
        repository.isRequestExpired(456L, requestType, 1.hours) shouldBe false

        repository.delete(entityId, requestType)

        repository.isRequestExpired(entityId, requestType, 1.hours) shouldBe true
        repository.isRequestExpired(entityId, "DIFFERENT_TYPE", 1.hours) shouldBe false
        repository.isRequestExpired(456L, requestType, 1.hours) shouldBe false
    }

    @Test
    fun `deleteAll should remove all cached requests`() {
        val now = fakeDateTimeProvider.now()

        repository.upsert(1L, "SHOW_DETAILS", now)
        repository.upsert(2L, "SIMILAR_SHOWS", now)
        repository.upsert(3L, "SEASON_DETAILS", now)

        repository.isRequestExpired(1L, "SHOW_DETAILS", 1.hours) shouldBe false
        repository.isRequestExpired(2L, "SIMILAR_SHOWS", 1.hours) shouldBe false
        repository.isRequestExpired(3L, "SEASON_DETAILS", 1.hours) shouldBe false

        repository.deleteAll()

        repository.isRequestExpired(1L, "SHOW_DETAILS", 1.hours) shouldBe true
        repository.isRequestExpired(2L, "SIMILAR_SHOWS", 1.hours) shouldBe true
        repository.isRequestExpired(3L, "SEASON_DETAILS", 1.hours) shouldBe true
    }

    @Test
    fun `should validate with RequestTypeConfig durations`() {
        val now = fakeDateTimeProvider.now()

        RequestTypeConfig.entries.forEach { config ->
            repository.upsert(config.requestId, config.name, now - (config.duration / 2))

            val isValid = repository.isRequestValid(config.name, config.duration)
            isValid shouldBe true

            repository.upsert(config.requestId, config.name, now - (config.duration + 1.hours))

            val isExpired = repository.isRequestValid(config.name, config.duration)
            isExpired shouldBe false
        }
    }

    private companion object {
        private val FIXED_NOW = Instant.fromEpochMilliseconds(1_700_000_000_000)
    }
}
