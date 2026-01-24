package com.thomaskioko.tvmaniac.core.networkutil.ratelimit

import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class AdaptiveApiRateLimiterTest {

    private lateinit var underTest: AdaptiveApiRateLimiter

    @BeforeTest
    fun setup() {
        underTest = AdaptiveApiRateLimiter()
    }

    @Test
    fun `should limit concurrent execution to max permits`() = runTest {
        val mutex = Mutex()
        var activeCount = 0
        var maxObservedConcurrency = 0
        val totalItems = 20

        val jobs = (1..totalItems).map {
            async {
                underTest.withPermit {
                    mutex.withLock {
                        activeCount++
                        if (activeCount > maxObservedConcurrency) {
                            maxObservedConcurrency = activeCount
                        }
                    }
                    delay(50)
                    mutex.withLock {
                        activeCount--
                    }
                }
            }
        }

        jobs.awaitAll()

        maxObservedConcurrency shouldBeLessThanOrEqual AdaptiveApiRateLimiter.MAX_CONCURRENT_API_CALLS
    }

    @Test
    fun `should process all items`() = runTest {
        val processedItems = mutableListOf<Int>()
        val mutex = Mutex()
        val totalItems = 15

        val jobs = (1..totalItems).map { index ->
            async {
                underTest.withPermit {
                    delay(10)
                    mutex.withLock {
                        processedItems.add(index)
                    }
                }
            }
        }

        jobs.awaitAll()

        processedItems.size shouldBe totalItems
    }

    @Test
    fun `should return result from block`() = runTest {
        val result = underTest.withPermit { 42 }

        result shouldBe 42
    }

    @Test
    fun `should increase backoff multiplier on rate limit`() = runTest {
        underTest.getBackoffMultiplier() shouldBe 0

        underTest.onRateLimited()
        underTest.getBackoffMultiplier() shouldBe 1

        underTest.onRateLimited()
        underTest.getBackoffMultiplier() shouldBe 2

        underTest.onRateLimited()
        underTest.getBackoffMultiplier() shouldBe 3
    }

    @Test
    fun `should not exceed max backoff multiplier`() = runTest {
        repeat(15) {
            underTest.onRateLimited()
        }

        underTest.getBackoffMultiplier() shouldBe AdaptiveApiRateLimiter.MAX_BACKOFF_MULTIPLIER
    }

    @Test
    fun `should decrease backoff after consecutive successes`() = runTest {
        underTest.onRateLimited()
        underTest.onRateLimited()
        underTest.onRateLimited()

        underTest.getBackoffMultiplier() shouldBe 3

        repeat(AdaptiveApiRateLimiter.SUCCESSES_BEFORE_RECOVERY) {
            underTest.onRequestSuccess()
        }

        underTest.getBackoffMultiplier() shouldBe 2
    }

    @Test
    fun `should reset consecutive successes on rate limit`() = runTest {
        underTest.onRateLimited()

        repeat(3) {
            underTest.onRequestSuccess()
        }

        underTest.getConsecutiveSuccesses() shouldBe 3

        underTest.onRateLimited()

        underTest.getConsecutiveSuccesses() shouldBe 0
    }

    @Test
    fun `should not decrease backoff below zero`() = runTest {
        underTest.getBackoffMultiplier() shouldBe 0

        repeat(20) {
            underTest.onRequestSuccess()
        }

        underTest.getBackoffMultiplier() shouldBe 0
    }

    @Test
    fun `should fully recover from max backoff`() = runTest {
        repeat(AdaptiveApiRateLimiter.MAX_BACKOFF_MULTIPLIER) {
            underTest.onRateLimited()
        }

        underTest.getBackoffMultiplier() shouldBe AdaptiveApiRateLimiter.MAX_BACKOFF_MULTIPLIER

        repeat(AdaptiveApiRateLimiter.MAX_BACKOFF_MULTIPLIER * AdaptiveApiRateLimiter.SUCCESSES_BEFORE_RECOVERY) {
            underTest.onRequestSuccess()
        }

        underTest.getBackoffMultiplier() shouldBe 0
    }

    @Test
    fun `should handle concurrent rate limit signals atomically`() = runTest {
        val jobs = (1..100).map {
            async {
                underTest.onRateLimited()
            }
        }

        jobs.awaitAll()

        underTest.getBackoffMultiplier() shouldBeLessThanOrEqual AdaptiveApiRateLimiter.MAX_BACKOFF_MULTIPLIER
    }

    @Test
    fun `should handle concurrent success signals atomically`() = runTest {
        underTest.onRateLimited()
        underTest.onRateLimited()
        underTest.onRateLimited()

        val jobs = (1..100).map {
            async {
                underTest.onRequestSuccess()
            }
        }

        jobs.awaitAll()

        underTest.getBackoffMultiplier() shouldBeLessThanOrEqual 3
        underTest.getBackoffMultiplier() shouldBe 0
    }
}
