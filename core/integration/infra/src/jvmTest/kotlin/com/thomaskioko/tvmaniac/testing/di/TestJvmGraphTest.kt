package com.thomaskioko.tvmaniac.testing.di

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.oauth.testing.FakeOAuthLauncher
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.implementation.DefaultSubscriptionManager
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

internal class TestJvmGraphTest {

    @Test
    fun `should provide fake OAuthLauncher`() = runTestWithGraph { graph ->
        graph.oAuthLauncher.shouldBeInstanceOf<FakeOAuthLauncher>()
    }

    @Test
    fun `should resolve RootPresenter factory`() = runTestWithGraph { graph ->
        val lifecycle = LifecycleRegistry().apply { resume() }
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        val presenter = graph.rootPresenterFactory(componentContext)

        presenter.shouldBeInstanceOf<RootPresenter>()

        // Tear down the lifecycle before the test exits so presenter-internal
        // coroutines that were launched on `Dispatchers.Main` complete before
        // `runTestWithGraph`'s `Dispatchers.resetMain()` fires.
        lifecycle.destroy()
        advanceUntilIdle()
    }

    @Test
    fun `should resolve NavDestinations set`() = runTestWithGraph { graph ->
        graph.navDestinations.shouldBeInstanceOf<Set<*>>()
    }

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
