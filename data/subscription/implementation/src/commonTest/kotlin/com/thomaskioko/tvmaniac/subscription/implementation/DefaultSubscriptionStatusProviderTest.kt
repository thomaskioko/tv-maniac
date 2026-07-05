package com.thomaskioko.tvmaniac.subscription.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatus
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultSubscriptionStatusProviderTest {

    private val provider = DefaultSubscriptionStatusProvider()

    @Test
    fun `should emit free status given the default provider`() = runTest {
        provider.observeStatus().test {
            awaitItem() shouldBe SubscriptionStatus.Free
            awaitComplete()
        }
    }
}
