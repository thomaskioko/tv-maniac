package com.thomaskioko.tvmaniac.subscription.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlag
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatus
import com.thomaskioko.tvmaniac.subscription.testing.FakeSubscriptionStatusProvider
import com.thomaskioko.tvmaniac.util.testing.FakeDebugConfig
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultSubscriptionManagerTest {

    private val enablePaywallFlag = FakeFeatureFlag(initial = false)
    private val subscriptionStatusProvider = FakeSubscriptionStatusProvider()
    private val datastoreRepository = FakeDatastoreRepository()

    private fun TestScope.createManager(isDebug: Boolean = true) = DefaultSubscriptionManager(
        enablePaywallFlag = enablePaywallFlag,
        subscriptionStatusProvider = subscriptionStatusProvider,
        datastoreRepository = datastoreRepository,
        debugConfig = FakeDebugConfig(isDebug = isDebug),
        scope = backgroundScope,
    )

    @Test
    fun `should grant access to every feature given the paywall flag is off`() = runTest {
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Free)
        val manager = createManager()

        SubscriptionFeature.entries.forEach { feature ->
            manager.observeAccess(feature).test {
                awaitItem() shouldBe true
            }
        }
    }

    @Test
    fun `should deny access given the paywall flag is on and the status is free`() = runTest {
        enablePaywallFlag.value = true
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Free)
        val manager = createManager()

        manager.observeAccess(SubscriptionFeature.Calendar).test {
            awaitItem() shouldBe false
        }
    }

    @Test
    fun `should grant access given the paywall flag is on and the status is premium`() = runTest {
        enablePaywallFlag.value = true
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Premium)
        val manager = createManager()

        manager.observeAccess(SubscriptionFeature.Calendar).test {
            awaitItem() shouldBe true
        }
    }

    @Test
    fun `should grant access given a premium override while debug is enabled`() = runTest {
        enablePaywallFlag.value = true
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Free)
        datastoreRepository.saveAccountType("Premium")
        val manager = createManager()

        manager.observeAccess(SubscriptionFeature.Calendar).test {
            awaitItem() shouldBe true
        }
    }

    @Test
    fun `should deny access given a free override while debug is enabled`() = runTest {
        enablePaywallFlag.value = true
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Premium)
        datastoreRepository.saveAccountType("Free")
        val manager = createManager()

        manager.observeAccess(SubscriptionFeature.Calendar).test {
            awaitItem() shouldBe false
        }
    }

    @Test
    fun `should ignore a premium override given debug is disabled`() = runTest {
        enablePaywallFlag.value = true
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Free)
        datastoreRepository.saveAccountType("Premium")
        val manager = createManager(isDebug = false)

        manager.observeAccess(SubscriptionFeature.Calendar).test {
            awaitItem() shouldBe false
        }
    }

    @Test
    fun `should deny access given an unknown status`() = runTest {
        enablePaywallFlag.value = true
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Unknown)
        val manager = createManager()

        manager.observeAccess(SubscriptionFeature.Calendar).test {
            awaitItem() shouldBe false
        }
    }

    @Test
    fun `should reflect the provider status regardless of the paywall flag`() = runTest {
        enablePaywallFlag.value = false
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Premium)
        val manager = createManager()

        manager.observeSubscriptionStatus().test {
            awaitItem() shouldBe SubscriptionStatus.Premium
        }
    }

    @Test
    fun `should apply the debug override to the reported status`() = runTest {
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Free)
        datastoreRepository.saveAccountType("Premium")
        val manager = createManager()

        manager.observeSubscriptionStatus().test {
            awaitItem() shouldBe SubscriptionStatus.Premium
        }
    }

    @Test
    fun `should pass an unknown status through unmapped`() = runTest {
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Unknown)
        val manager = createManager()

        manager.observeSubscriptionStatus().test {
            awaitItem() shouldBe SubscriptionStatus.Unknown
        }
    }

    @Test
    fun `should return the latest access value given a one shot check`() = runTest {
        enablePaywallFlag.value = true
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Premium)
        val manager = createManager()

        manager.hasAccess(SubscriptionFeature.Calendar) shouldBe true
    }

    @Test
    fun `should emit an updated access value given the override changes after first read`() = runTest {
        enablePaywallFlag.value = true
        subscriptionStatusProvider.setStatus(SubscriptionStatus.Free)
        val manager = createManager()

        manager.observeAccess(SubscriptionFeature.Calendar).test {
            awaitItem() shouldBe false

            datastoreRepository.saveAccountType("Premium")

            awaitItem() shouldBe true
        }
    }
}
