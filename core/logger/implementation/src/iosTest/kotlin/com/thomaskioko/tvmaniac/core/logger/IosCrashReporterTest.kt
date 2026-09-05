package com.thomaskioko.tvmaniac.core.logger

import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

private class FakeCrashlytics : Crashlytics {
    private val customValuesMap: MutableMap<String, String> = mutableMapOf()
    private val messagesList: MutableList<String> = mutableListOf()
    var recordedException: Throwable? = null
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
    }

    override fun setUserId(userId: String) {
        recordedUserId = userId
    }

    override fun logMessage(message: String) {
        messagesList += message
    }
}

private class FakeCrashlyticsCollection : CrashlyticsCollection {
    var enabled: Boolean? = null
        private set

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}

class IosCrashReporterTest {

    private val fakeCrashlytics = FakeCrashlytics()
    private val fakeCollection = FakeCrashlyticsCollection()
    private val reporter = IosCrashReporter(fakeCrashlytics, fakeCollection)

    @Test
    fun `should send handled exception with custom values given keys`() {
        val throwable = IllegalStateException("boom")

        reporter.recordException(
            throwable,
            mapOf(CrashReportKeys.TAG to "Network", CrashReportKeys.SCREEN to "Home"),
        )

        fakeCrashlytics.recordedException shouldBe throwable
        fakeCrashlytics.customValues shouldContainExactly mapOf(CrashReportKeys.TAG to "Network", CrashReportKeys.SCREEN to "Home")
    }

    @Test
    fun `should log a breadcrumb`() {
        reporter.log("navigated to Home")

        fakeCrashlytics.messages shouldBe listOf("navigated to Home")
    }

    @Test
    fun `should forward the collection toggle to Firebase`() {
        reporter.setCollectionEnabled(false)

        fakeCollection.enabled shouldBe false
    }
}
