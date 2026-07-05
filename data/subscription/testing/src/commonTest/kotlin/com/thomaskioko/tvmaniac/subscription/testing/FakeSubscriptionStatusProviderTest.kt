package com.thomaskioko.tvmaniac.subscription.testing

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatus
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class FakeSubscriptionStatusProviderTest {

    private val provider = FakeSubscriptionStatusProvider()

    @Test
    fun `should drive observeStatus to premium given setStatus is called with premium`() = runTest {
        provider.observeStatus().test {
            awaitItem() shouldBe SubscriptionStatus.Free

            provider.setStatus(SubscriptionStatus.Premium)
            awaitItem() shouldBe SubscriptionStatus.Premium
        }
    }
}
