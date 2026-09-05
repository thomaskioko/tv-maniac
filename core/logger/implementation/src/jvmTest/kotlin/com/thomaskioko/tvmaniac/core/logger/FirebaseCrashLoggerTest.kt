package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashReporter
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class FirebaseCrashLoggerTest {

    private val crashReporter = FakeCrashReporter()
    private val firebaseCrashLogger = FirebaseCrashLogger(crashReporter)

    @Test
    fun `should record exception once with keys and tag given error with throwable`() {
        val throwable = IllegalStateException("boom")

        firebaseCrashLogger.error(
            tag = "Network",
            message = "Request failed",
            throwable = throwable,
            keys = mapOf("endpoint" to "/shows/{id}", "status" to "500"),
        )

        crashReporter.recordedExceptions shouldHaveSize 1
        val recorded = crashReporter.recordedExceptions.first()
        recorded.throwable shouldBe throwable
        recorded.keys shouldBe mapOf("endpoint" to "/shows/{id}", "status" to "500", "tag" to "Network")
        crashReporter.breadcrumbs shouldBe listOf("[Network] Request failed")
    }

    @Test
    fun `should write breadcrumb only given warning with throwable`() {
        val throwable = IllegalStateException("timeout")

        firebaseCrashLogger.warning(tag = "Network", message = "Request timed out", throwable = throwable)

        crashReporter.recordedExceptions.shouldBeEmpty()
        crashReporter.breadcrumbs shouldHaveSize 1
        crashReporter.breadcrumbs.first() shouldBe "[Network] Request timed out: IllegalStateException: timeout"
    }

    @Test
    fun `should write breadcrumb with repeat and skip report given duplicate failure`() {
        val throwable = IllegalStateException("boom")
        val keys = mapOf("endpoint" to "/shows/{id}", "status" to "500")

        firebaseCrashLogger.error(tag = "Network", message = "Request failed", throwable = throwable, keys = keys)
        firebaseCrashLogger.error(tag = "Network", message = "Request failed", throwable = throwable, keys = keys)

        crashReporter.recordedExceptions shouldHaveSize 1
        crashReporter.breadcrumbs shouldHaveSize 2
        crashReporter.breadcrumbs.last() shouldContain "repeat=true"
    }

    @Test
    fun `should write breadcrumb given two argument error`() {
        firebaseCrashLogger.error(tag = "Network", message = "Request failed")

        crashReporter.recordedExceptions.shouldBeEmpty()
        crashReporter.breadcrumbs shouldBe listOf("[Network] Request failed")
    }
}
