package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.StackNavigation
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class DefaultNavigatorTest {

    @Test
    fun `should emit one event given pushNew`() {
        val navigator = DefaultNavigator()

        val events = navigator.getStackNavigation().collectEvents {
            navigator.pushNew(ShowDetailsRoute(ShowDetailsParam(1)))
        }

        events.size shouldBe 1
    }

    @Test
    fun `should emit one event given bringToFront`() {
        val navigator = DefaultNavigator()

        val events = navigator.getStackNavigation().collectEvents {
            navigator.bringToFront(HomeRoute)
        }

        events.size shouldBe 1
    }

    @Test
    fun `should emit one event given pushToFront`() {
        val navigator = DefaultNavigator()

        val events = navigator.getStackNavigation().collectEvents {
            navigator.pushToFront(HomeRoute)
        }

        events.size shouldBe 1
    }

    @Test
    fun `should emit one event given pop`() {
        val navigator = DefaultNavigator()

        val events = navigator.getStackNavigation().collectEvents {
            navigator.pop()
        }

        events.size shouldBe 1
    }

    @Test
    fun `should emit one event given popTo`() {
        val navigator = DefaultNavigator()

        val events = navigator.getStackNavigation().collectEvents {
            navigator.popTo(0)
        }

        events.size shouldBe 1
    }

    @Test
    fun `should return same StackNavigation instance on repeated calls`() {
        val navigator = DefaultNavigator()

        val first = navigator.getStackNavigation()
        val second = navigator.getStackNavigation()

        (first === second) shouldBe true
    }

    @Test
    fun `should transform stack to contain pushed route given pushNew`() {
        val navigator = DefaultNavigator()

        val events = navigator.getStackNavigation().collectEvents {
            navigator.pushNew(ShowDetailsRoute(ShowDetailsParam(42)))
        }

        val transformed = events.single().transformer(listOf(HomeRoute))
        transformed shouldBe listOf(HomeRoute, ShowDetailsRoute(ShowDetailsParam(42)))
    }

    @Test
    fun `should transform stack by dropping last given pop`() {
        val navigator = DefaultNavigator()
        val route = ShowDetailsRoute(ShowDetailsParam(1))

        val events = navigator.getStackNavigation().collectEvents {
            navigator.pop()
        }

        val transformed = events.single().transformer(listOf(HomeRoute, route))
        transformed shouldBe listOf(HomeRoute)
    }

    private fun StackNavigation<NavRoute>.collectEvents(
        action: () -> Unit,
    ): List<StackNavigation.Event<NavRoute>> {
        val received = mutableListOf<StackNavigation.Event<NavRoute>>()
        val cancellation = subscribe { received += it }
        try {
            action()
        } finally {
            cancellation.cancel()
        }
        return received
    }
}
