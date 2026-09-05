package com.thomaskioko.tvmaniac.domain.telemetry

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashReporter
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatus
import com.thomaskioko.tvmaniac.subscription.testing.FakeSubscriptionManager
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TelemetryContextInitializerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val collectScope = CoroutineScope(testDispatcher + Job())

    private val accountManager = FakeAccountManager()
    private val subscriptionManager = FakeSubscriptionManager()
    private val connectionChecker = FakeInternetConnectionChecker()
    private val crashReporter = FakeCrashReporter()

    private val initializer = TelemetryContextInitializer(
        accountManager = accountManager,
        subscriptionManager = subscriptionManager,
        connectionChecker = connectionChecker,
        crashReporter = crashReporter,
        coroutineScope = collectScope,
    )

    @AfterTest
    fun tearDown() {
        collectScope.cancel()
    }

    @Test
    fun `should set provider key to none given no active provider`() = runTest(testDispatcher) {
        initializer.init()
        runCurrent()

        crashReporter.customKeys[CrashReportKeys.PROVIDER] shouldBe "none"
    }

    @Test
    fun `should update provider key given the active provider changes`() = runTest(testDispatcher) {
        initializer.init()
        runCurrent()

        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        runCurrent()
        crashReporter.customKeys[CrashReportKeys.PROVIDER] shouldBe "trakt"

        accountManager.setActiveProvider(SyncProviderSource.SIMKL)
        runCurrent()
        crashReporter.customKeys[CrashReportKeys.PROVIDER] shouldBe "simkl"

        accountManager.setActiveProvider(null)
        runCurrent()
        crashReporter.customKeys[CrashReportKeys.PROVIDER] shouldBe "none"
    }

    @Test
    fun `should update signed_in key given the connected account changes`() = runTest(testDispatcher) {
        initializer.init()
        runCurrent()

        crashReporter.customKeys[CrashReportKeys.SIGNED_IN] shouldBe "false"

        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        runCurrent()

        crashReporter.customKeys[CrashReportKeys.SIGNED_IN] shouldBe "true"
    }

    @Test
    fun `should update premium key given the subscription status changes`() = runTest(testDispatcher) {
        initializer.init()
        runCurrent()

        crashReporter.customKeys[CrashReportKeys.PREMIUM] shouldBe "false"

        subscriptionManager.setStatus(SubscriptionStatus.Premium)
        runCurrent()

        crashReporter.customKeys[CrashReportKeys.PREMIUM] shouldBe "true"
    }

    @Test
    fun `should update connectivity key given the connection state changes`() = runTest(testDispatcher) {
        initializer.init()
        runCurrent()

        crashReporter.customKeys[CrashReportKeys.CONNECTIVITY] shouldBe "online"

        connectionChecker.setConnected(false)
        runCurrent()

        crashReporter.customKeys[CrashReportKeys.CONNECTIVITY] shouldBe "offline"
    }

    @Test
    fun `should never set a user id given any context change`() = runTest(testDispatcher) {
        initializer.init()
        runCurrent()

        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        subscriptionManager.setStatus(SubscriptionStatus.Premium)
        connectionChecker.setConnected(false)
        runCurrent()

        crashReporter.userId shouldBe null
    }
}
