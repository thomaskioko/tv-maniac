package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashlyticsConfiguration
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

private class FakeCrashlytics : Crashlytics {
    private val customValuesMap: MutableMap<String, String> = mutableMapOf()
    private val messagesList: MutableList<String> = mutableListOf()
    var recordedException: Throwable? = null
        private set
    var valuesAtSend: Map<String, String> = emptyMap()
        private set
    var recordedUserId: String? = null
        private set

    val customValues: Map<String, String> get() = customValuesMap.toMap()
    val messages: List<String> get() = messagesList.toList()

    override fun setCustomValue(key: String, value: String) {
        customValuesMap[key] = value
    }

    override fun sendHandledException(throwable: Throwable) {
        recordedException = throwable
        valuesAtSend = customValuesMap.toMap()
    }

    override fun setUserId(userId: String) {
        recordedUserId = userId
    }

    override fun logMessage(message: String) {
        messagesList += message
    }
}

class IosCrashReporterTest {

    private val fakeCrashlytics = FakeCrashlytics()
    private val configuration = FakeCrashlyticsConfiguration()
    private val reporter = IosCrashReporter(fakeCrashlytics, configuration)

    @Test
    fun `should send handled exception with custom values given keys`() {
        val throwable = IllegalStateException("boom")

        reporter.recordException(
            throwable,
            mapOf(CrashReportKeys.TAG to "Network", CrashReportKeys.SCREEN to "Home"),
        )

        fakeCrashlytics.recordedException shouldBe throwable
        fakeCrashlytics.valuesAtSend shouldContainExactly mapOf(CrashReportKeys.TAG to "Network", CrashReportKeys.SCREEN to "Home")
    }

    @Test
    fun `should log a breadcrumb given a message`() {
        reporter.log("navigated to Home")

        fakeCrashlytics.messages shouldBe listOf("navigated to Home")
    }

    @Test
    fun `should forward the collection toggle to Firebase given Firebase is configured`() {
        reporter.setCollectionEnabled(false)

        configuration.enabled shouldBe false
    }

    @Test
    fun `should not touch Firebase given it is not configured`() {
        val unconfigured = FakeCrashlyticsConfiguration(isConfigured = false)
        val unconfiguredReporter = IosCrashReporter(fakeCrashlytics, unconfigured)

        unconfiguredReporter.setCollectionEnabled(false)

        unconfigured.enabled shouldBe null
    }

    @Test
    fun `should clear the report keys after sending given keys`() {
        reporter.recordException(IllegalStateException("boom"), mapOf(CrashReportKeys.ENDPOINT to "shows/{id}"))

        fakeCrashlytics.customValues shouldBe mapOf(CrashReportKeys.ENDPOINT to "")
    }

    @Test
    fun `should send the event value and restore the launch value given an event key set by a custom key`() {
        reporter.setCustomKey("provider", "trakt")

        reporter.recordException(IllegalStateException("boom"), mapOf("provider" to "tmdb"))

        fakeCrashlytics.valuesAtSend shouldContainExactly mapOf("provider" to "tmdb")
        fakeCrashlytics.customValues shouldBe mapOf("provider" to "trakt")
    }

    @Test
    fun `should clear only the event key never set by a custom key given a report with two keys`() {
        reporter.setCustomKey("provider", "trakt")

        reporter.recordException(IllegalStateException("boom"), mapOf("provider" to "tmdb", "endpoint" to "shows/{id}"))

        fakeCrashlytics.customValues shouldBe mapOf("provider" to "trakt", "endpoint" to "")
    }
}
