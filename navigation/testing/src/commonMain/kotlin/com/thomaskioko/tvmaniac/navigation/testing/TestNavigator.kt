package com.thomaskioko.tvmaniac.navigation.testing

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.navigation.BaseRoute
import com.thomaskioko.tvmaniac.navigation.MultiStackHostState
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.reflect.KClass

/**
 * [Navigator] implementation for unit tests that records every call as a [NavEvent] and exposes
 * them through [events]. No real stack mutation happens; feature-specific navigators that delegate
 * to [Navigator] can be connected directly to this fake to assert on the routes they push.
 *
 * Consume events through [Navigator.test] or [Navigator.testIn] rather than subscribing to
 * [events] directly so unconsumed events fail the enclosing test.
 *
 * Pass [initialActiveRoot] when the test observes [activeRoot]. Otherwise the navigator reports
 * [UnspecifiedNavRoot] until the first [switchBackStack], [showRoot], or [replaceAllBackStacks]
 * call.
 *
 * @param initialActiveRoot value reported by [activeRoot] until a switch, show, or replace call
 *   updates it.
 */
public class TestNavigator(
    initialActiveRoot: NavRoot = UnspecifiedNavRoot,
) : Navigator {

    private val _events = MutableSharedFlow<NavEvent>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    /** Navigation calls recorded since this navigator was created, in order. */
    public val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    private val activeRootValue: MutableValue<NavRoot> = MutableValue(initialActiveRoot)
    private val overlayNavigation = SlotNavigation<NavRoute>()

    override val activeRoot: Value<NavRoot> get() = activeRootValue

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
        activeRootValue.value = root
        _events.tryEmit(NavEvent.SwitchBackStack(root))
    }

    override fun showRoot(root: NavRoot) {
        activeRootValue.value = root
        _events.tryEmit(NavEvent.ShowRoot(root))
    }

    override fun replaceAllBackStacks(root: NavRoot) {
        activeRootValue.value = root
        _events.tryEmit(NavEvent.ReplaceAllBackStacks(root))
    }

    override fun <T : Any> buildHostNavigation(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (BaseRoute, ComponentContext) -> T,
    ): Value<MultiStackHostState<T>> {
        activeRootValue.value = initialRoot
        val rootInstance = childFactory(initialRoot as BaseRoute, componentContext)
        val initialChildStack = ChildStack(
            active = Child.Created(configuration = initialRoot, instance = rootInstance),
        )
        return MutableValue(
            MultiStackHostState(
                activeRoot = initialRoot,
                tabStacks = mapOf(initialRoot to initialChildStack),
            ),
        )
    }

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

    override fun dismissOverlay() {
        overlayNavigation.dismiss()
    }
}
