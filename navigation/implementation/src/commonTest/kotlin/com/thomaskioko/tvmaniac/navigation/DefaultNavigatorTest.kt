package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.serialization.Serializable

internal class DefaultNavigatorTest {

    @Serializable
    private data object PrimaryRoot : NavRoot

    @Test
    fun `should push new route on top of stack given pushNew`() {
        val (navigator, stack) = newNavigator()

        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(42)))

        stack.value.active.configuration shouldBe ShowDetailsRoute(ShowDetailsParam(42))
        stack.value.backStack.map { it.configuration } shouldBe listOf(PrimaryRoot)
    }

    @Test
    fun `should pop top entry given pop`() {
        val (navigator, stack) = newNavigator()
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(1)))

        navigator.navigateBack()

        stack.value.active.configuration shouldBe PrimaryRoot
        stack.value.backStack.shouldBeEmpty()
    }

    @Test
    fun `should reuse existing entry given bringToFront for already pushed route`() {
        val (navigator, stack) = newNavigator()
        val first = ShowDetailsRoute(ShowDetailsParam(7))
        val second = ShowDetailsRoute(ShowDetailsParam(8))
        navigator.navigateTo(first)
        navigator.navigateTo(second)

        navigator.bringToFront(first)

        stack.value.active.configuration shouldBe first
    }

    @Test
    fun `should pop back to most recent matching route given navigateBackTo target route type`() {
        val (navigator, stack) = newNavigator()
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(1)))
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(2)))
        navigator.navigateTo(MoreShowsRoute(99))

        navigator.navigateBackTo<ShowDetailsRoute>()

        stack.value.active.configuration shouldBe ShowDetailsRoute(ShowDetailsParam(2))
    }

    @Test
    fun `should leave stack unchanged given navigateBackTo for absent route type`() {
        val (navigator, stack) = newNavigator()
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(1)))

        navigator.navigateBackTo(MoreShowsRoute::class)

        stack.value.active.configuration shouldBe ShowDetailsRoute(ShowDetailsParam(1))
    }

    @Test
    fun `should replace existing entry of same class given bringToFront with different param`() {
        val (navigator, stack) = newNavigator()
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(7)))

        navigator.bringToFront(ShowDetailsRoute(ShowDetailsParam(8)))

        stack.value.active.configuration shouldBe ShowDetailsRoute(ShowDetailsParam(8))
        stack.value.backStack.map { it.configuration } shouldBe listOf(PrimaryRoot)
    }

    @Test
    fun `should move existing entry to top given pushToFront with equal route`() {
        val (navigator, stack) = newNavigator()
        val first = ShowDetailsRoute(ShowDetailsParam(7))
        val second = ShowDetailsRoute(ShowDetailsParam(8))
        navigator.navigateTo(first)
        navigator.navigateTo(second)

        navigator.pushToFront(first)

        stack.value.active.configuration shouldBe first
        stack.value.backStack.map { it.configuration } shouldBe listOf(PrimaryRoot, second)
    }

    @Test
    fun `should keep existing entry given pushToFront with different param of same class`() {
        val (navigator, stack) = newNavigator()
        val existing = ShowDetailsRoute(ShowDetailsParam(7))
        val next = ShowDetailsRoute(ShowDetailsParam(8))
        navigator.navigateTo(existing)

        navigator.pushToFront(next)

        stack.value.active.configuration shouldBe next
        stack.value.backStack.map { it.configuration } shouldBe listOf(PrimaryRoot, existing)
    }

    @Test
    fun `should leave only root given popTo zero`() {
        val (navigator, stack) = newNavigator()
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(1)))
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(2)))
        navigator.navigateTo(MoreShowsRoute(99))

        navigator.popTo(0)

        stack.value.active.configuration shouldBe PrimaryRoot
        stack.value.backStack.shouldBeEmpty()
    }

    @Test
    fun `should keep prefix given popTo intermediate index`() {
        val (navigator, stack) = newNavigator()
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(1)))
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(2)))
        navigator.navigateTo(MoreShowsRoute(99))

        navigator.popTo(1)

        stack.value.active.configuration shouldBe ShowDetailsRoute(ShowDetailsParam(1))
        stack.value.backStack.map { it.configuration } shouldBe listOf(PrimaryRoot)
    }

    @Test
    fun `should leave stack unchanged given popTo at current top index`() {
        val (navigator, stack) = newNavigator()
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(1)))
        navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(2)))

        navigator.popTo(2)

        stack.value.active.configuration shouldBe ShowDetailsRoute(ShowDetailsParam(2))
        stack.value.backStack.map { it.configuration } shouldBe listOf(PrimaryRoot, ShowDetailsRoute(ShowDetailsParam(1)))
    }

    private fun newNavigator(): Pair<Navigator, Value<ChildStack<BaseRoute, BaseRoute>>> {
        val routeBindings = setOf<NavRouteBinding<*>>(
            NavRouteBinding(ShowDetailsRoute::class, ShowDetailsRoute.serializer()),
            NavRouteBinding(MoreShowsRoute::class, MoreShowsRoute.serializer()),
        )
        val rootBindings = setOf<NavRootBinding<*>>(
            NavRootBinding(PrimaryRoot::class, PrimaryRoot.serializer()),
        )
        val navigator = DefaultNavigator(
            navRouteSerializer = DefaultNavRouteSerializer(routeBindings),
            navRootSerializer = DefaultNavRootSerializer(rootBindings),
            baseRouteSerializer = DefaultBaseRouteSerializer(routeBindings, rootBindings),
            navRoots = setOf(PrimaryRoot),
        )
        val lifecycle = LifecycleRegistry().apply { resume() }
        val context = DefaultComponentContext(lifecycle = lifecycle)
        val hostState = navigator.buildHostNavigation(
            componentContext = context,
            initialRoot = PrimaryRoot,
            childFactory = { route, _ -> route },
        )
        val stack: Value<ChildStack<BaseRoute, BaseRoute>> =
            hostState.map { it.tabStacks.getValue(PrimaryRoot) }
        return navigator to stack
    }
}
