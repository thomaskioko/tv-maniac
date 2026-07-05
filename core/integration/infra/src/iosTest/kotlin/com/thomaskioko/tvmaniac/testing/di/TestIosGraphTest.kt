package com.thomaskioko.tvmaniac.testing.di

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.implementation.DefaultSubscriptionManager
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

internal class TestIosGraphTest {

    @Test
    fun `should resolve the production DefaultSubscriptionManager binding`() = runTestWithGraph { graph ->
        graph.subscriptionManager.shouldBeInstanceOf<DefaultSubscriptionManager>()
    }

    @Test
    fun `should grant access to every premium feature given the paywall flag defaults to off`() = runTestWithGraph { graph ->
        SubscriptionFeature.entries.forEach { feature ->
            graph.subscriptionManager.observeAccess(feature).test {
                awaitItem() shouldBe true
            }
        }
    }
}
