package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashReporter
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CompositeCrashReporterTest {

    private val first = FakeCrashReporter()
    private val second = FakeCrashReporter()
    private val reporter = CompositeCrashReporter(setOf(first, second))

    @Test
    fun `should forward the collection toggle to every reporter given a toggle`() {
        reporter.setCollectionEnabled(false)

        first.isCollectionEnabled shouldBe false
        second.isCollectionEnabled shouldBe false
    }

    @Test
    fun `should forward the exception and keys to every reporter given a report`() {
        val throwable = IllegalStateException("boom")
        val keys = mapOf(CrashReportKeys.TAG to "Network")

        reporter.recordException(throwable, keys)

        listOf(first, second).forEach {
            it.recordedExceptions.map { recorded -> recorded.throwable } shouldBe listOf(throwable)
            it.recordedExceptions.map { recorded -> recorded.keys } shouldBe listOf(keys)
        }
    }

    @Test
    fun `should forward empty keys to every reporter given a report without keys`() {
        val throwable = IllegalStateException("boom")

        reporter.recordException(throwable)

        listOf(first, second).forEach {
            it.recordedExceptions.map { recorded -> recorded.keys } shouldBe listOf(emptyMap())
        }
    }

    @Test
    fun `should forward the custom key to every reporter given a custom key`() {
        reporter.setCustomKey("provider", "trakt")

        first.customKeys shouldBe mapOf("provider" to "trakt")
        second.customKeys shouldBe mapOf("provider" to "trakt")
    }

    @Test
    fun `should forward the user id to every reporter given a user id`() {
        reporter.setUserId("user-1")

        first.userId shouldBe "user-1"
        second.userId shouldBe "user-1"
    }

    @Test
    fun `should forward the breadcrumb to every reporter given a log message`() {
        reporter.log("navigated to Home")

        first.breadcrumbs shouldBe listOf("navigated to Home")
        second.breadcrumbs shouldBe listOf("navigated to Home")
    }
}
