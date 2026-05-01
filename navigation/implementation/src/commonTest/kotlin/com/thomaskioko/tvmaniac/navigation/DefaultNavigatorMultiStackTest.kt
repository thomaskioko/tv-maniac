package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test
import kotlinx.serialization.Serializable

internal class DefaultNavigatorMultiStackTest {

    @Serializable
    private data object DiscoverTestRoot : NavRoot

    @Serializable
    private data object LibraryTestRoot : NavRoot

    @Serializable
    private data object DetailRoute : NavRoute

    @Serializable
    private data object OverlayDialogRoute : NavRoute, OverlayRoute

    @Test
    fun `should push to active tab stack given navigateTo with screen route`() {
        val (navigator, _, libraryStack) = newMultiStackNavigator(initialRoot = LibraryTestRoot)

        navigator.navigateTo(DetailRoute)

        libraryStack.value.active.configuration shouldBe DetailRoute
    }

    @Test
    fun `should preserve other tabs given navigateTo on active tab`() {
        val (navigator, discoverStack, libraryStack) = newMultiStackNavigator(initialRoot = LibraryTestRoot)

        navigator.navigateTo(DetailRoute)

        discoverStack.value.active.configuration shouldBe DiscoverTestRoot
        libraryStack.value.active.configuration shouldBe DetailRoute
    }

    @Test
    fun `should preserve target stack given switchBackStack to inactive tab`() {
        val (navigator, _, libraryStack) = newMultiStackNavigator(initialRoot = LibraryTestRoot)
        navigator.navigateTo(DetailRoute)
        libraryStack.value.active.configuration shouldBe DetailRoute

        navigator.switchBackStack(DiscoverTestRoot)
        navigator.switchBackStack(LibraryTestRoot)

        libraryStack.value.active.configuration shouldBe DetailRoute
    }

    @Test
    fun `should clear target stack given showRoot`() {
        val (navigator, _, libraryStack) = newMultiStackNavigator(initialRoot = LibraryTestRoot)
        navigator.navigateTo(DetailRoute)
        libraryStack.value.active.configuration shouldBe DetailRoute

        navigator.showRoot(LibraryTestRoot)

        libraryStack.value.active.configuration shouldBe LibraryTestRoot
    }

    @Test
    fun `should activate overlay slot given navigateTo with OverlayRoute`() {
        val (navigator, _, _, overlaySlot) = newMultiStackNavigatorWithOverlay()

        navigator.navigateTo(OverlayDialogRoute)

        overlaySlot.value.child shouldNotBe null
        overlaySlot.value.child?.configuration?.shouldBeInstanceOf<OverlayDialogRoute>()
    }

    private data class MultiStackNavigatorBundle(
        val navigator: Navigator,
        val discoverStack: Value<ChildStack<*, BaseRoute>>,
        val libraryStack: Value<ChildStack<*, BaseRoute>>,
    )

    private data class OverlayNavigatorBundle(
        val navigator: Navigator,
        val discoverStack: Value<ChildStack<*, BaseRoute>>,
        val libraryStack: Value<ChildStack<*, BaseRoute>>,
        val overlaySlot: Value<ChildSlot<*, NavRoute>>,
    )

    private fun newMultiStackNavigator(initialRoot: NavRoot): MultiStackNavigatorBundle {
        val navigator = newNavigator()
        val context = newContext()
        val discoverStack = navigator.buildTabStack(
            componentContext = context,
            root = DiscoverTestRoot,
            childFactory = { route, _ -> route },
        )
        val libraryStack = navigator.buildTabStack(
            componentContext = context,
            root = LibraryTestRoot,
            childFactory = { route, _ -> route },
        )
        navigator.buildRootStack(
            componentContext = context,
            initialRoot = initialRoot,
            childFactory = { root, _ -> root },
        )
        return MultiStackNavigatorBundle(navigator, discoverStack, libraryStack)
    }

    private fun newMultiStackNavigatorWithOverlay(): OverlayNavigatorBundle {
        val navigator = newNavigator()
        val context = newContext()
        val discoverStack = navigator.buildTabStack(
            componentContext = context,
            root = DiscoverTestRoot,
            childFactory = { route, _ -> route },
        )
        val libraryStack = navigator.buildTabStack(
            componentContext = context,
            root = LibraryTestRoot,
            childFactory = { route, _ -> route },
        )
        navigator.buildRootStack(
            componentContext = context,
            initialRoot = LibraryTestRoot,
            childFactory = { root, _ -> root },
        )
        val overlaySlot = navigator.buildOverlaySlot(
            componentContext = context,
            childFactory = { route, _ -> route },
        )
        return OverlayNavigatorBundle(navigator, discoverStack, libraryStack, overlaySlot)
    }

    private fun newNavigator(): Navigator {
        val routeBindings = setOf<NavRouteBinding<*>>(
            NavRouteBinding(DetailRoute::class, DetailRoute.serializer()),
            NavRouteBinding(OverlayDialogRoute::class, OverlayDialogRoute.serializer()),
        )
        val rootBindings = setOf<NavRootBinding<*>>(
            NavRootBinding(DiscoverTestRoot::class, DiscoverTestRoot.serializer()),
            NavRootBinding(LibraryTestRoot::class, LibraryTestRoot.serializer()),
        )
        val navRoots = setOf<NavRoot>(DiscoverTestRoot, LibraryTestRoot)
        return DefaultNavigator(
            navRouteSerializer = DefaultNavRouteSerializer(routeBindings),
            navRootSerializer = DefaultNavRootSerializer(rootBindings),
            baseRouteSerializer = DefaultBaseRouteSerializer(routeBindings, rootBindings),
            navRoots = navRoots,
        )
    }

    private fun newContext(): DefaultComponentContext {
        val lifecycle = LifecycleRegistry().apply { resume() }
        return DefaultComponentContext(lifecycle = lifecycle)
    }
}
