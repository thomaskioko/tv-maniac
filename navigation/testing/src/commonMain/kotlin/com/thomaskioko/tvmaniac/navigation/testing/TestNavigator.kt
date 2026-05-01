package com.thomaskioko.tvmaniac.navigation.testing

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.navigation.BaseRoute
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import kotlin.reflect.KClass
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * [Navigator] implementation for unit tests that records every call as a [NavEvent] and exposes
 * them via [events]. No real stack mutation happens (apart from internal sources used only when
 * one of the `build*` methods is invoked); feature-specific navigators that delegate to
 * [Navigator] can be wired directly to this fake to assert on the routes they push.
 *
 * Consume events through [Navigator.test] or [Navigator.testIn] rather than subscribing to
 * [events] directly so unconsumed events fail the enclosing test.
 */
public class TestNavigator : Navigator {

    private val _events = MutableSharedFlow<NavEvent>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    /** Navigation calls recorded since this navigator was created, in order. */
    public val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    private val rootNavigation = StackNavigation<NavRoot>()
    private val tabStacks = mutableMapOf<NavRoot, StackNavigation<BaseRoute>>()
    private val overlayNavigation = SlotNavigation<NavRoute>()

    override fun navigateTo(route: NavRoute) {
        _events.tryEmit(NavEvent.NavigateTo(route))
    }

    override fun navigateBack() {
        _events.tryEmit(NavEvent.NavigateBack)
    }

    override fun navigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean) {
        _events.tryEmit(NavEvent.NavigateBackTo(routeClass, inclusive))
    }

    override fun popTo(toIndex: Int) {
        _events.tryEmit(NavEvent.PopTo(toIndex))
    }

    override fun bringToFront(route: NavRoute) {
        _events.tryEmit(NavEvent.BringToFront(route))
    }

    override fun pushToFront(route: NavRoute) {
        _events.tryEmit(NavEvent.PushToFront(route))
    }

    override fun switchBackStack(root: NavRoot) {
        _events.tryEmit(NavEvent.SwitchBackStack(root))
    }

    override fun showRoot(root: NavRoot) {
        _events.tryEmit(NavEvent.ShowRoot(root))
    }

    override fun replaceAllBackStacks(root: NavRoot) {
        _events.tryEmit(NavEvent.ReplaceAllBackStacks(root))
    }

    override fun <T : Any> buildRootStack(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (NavRoot, ComponentContext) -> T,
    ): Value<ChildStack<*, T>> = componentContext.childStack(
        source = rootNavigation,
        serializer = null,
        initialConfiguration = initialRoot,
        key = "TestNavigatorRootTabStackKey",
        handleBackButton = false,
        childFactory = childFactory,
    )

    override fun <T : Any> buildOverlaySlot(
        componentContext: ComponentContext,
        childFactory: (NavRoute, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>> = componentContext.childSlot(
        source = overlayNavigation,
        serializer = null,
        key = "TestNavigatorOverlaySlotKey",
        handleBackButton = true,
        childFactory = childFactory,
    )

    override fun <T : Any> buildTabStack(
        componentContext: ComponentContext,
        root: NavRoot,
        childFactory: (BaseRoute, ComponentContext) -> T,
    ): Value<ChildStack<*, T>> {
        val source = tabStacks.getOrPut(root) { StackNavigation() }
        return componentContext.childStack(
            source = source,
            serializer = null,
            initialConfiguration = root as BaseRoute,
            key = "TestNavigatorTabStack_${root::class.simpleName}",
            handleBackButton = true,
            childFactory = childFactory,
        )
    }
}
