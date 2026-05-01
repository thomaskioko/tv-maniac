package com.thomaskioko.tvmaniac.navigation.testing

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.navigation.BaseRoute
import com.thomaskioko.tvmaniac.navigation.MultiStackHostState
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import kotlin.reflect.KClass

/**
 * [Navigator] that ignores every navigation call. Use in presenter tests where the test does not
 * assert on navigation events (only on state, side effects, etc.). Tests that want to verify
 * navigation calls should use [TestNavigator] instead.
 *
 * [activeRoot] returns [UnspecifiedNavRoot] unless an explicit [initialActiveRoot] is supplied.
 * [buildHostNavigation] returns a constant [Value] containing only the initial root entry; no
 * mutation calls are recorded.
 */
public class NoOpNavigator(
    private val initialActiveRoot: NavRoot = UnspecifiedNavRoot,
) : Navigator {

    private val activeRootValue: Value<NavRoot> = MutableValue(initialActiveRoot)
    private val overlayNavigation = SlotNavigation<NavRoute>()

    override val activeRoot: Value<NavRoot> get() = activeRootValue

    override fun navigateTo(route: NavRoute): Unit = Unit

    override fun navigateBack(): Unit = Unit

    override fun navigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean): Unit = Unit

    override fun popTo(toIndex: Int): Unit = Unit

    override fun bringToFront(route: NavRoute): Unit = Unit

    override fun pushToFront(route: NavRoute): Unit = Unit

    override fun switchBackStack(root: NavRoot): Unit = Unit

    override fun showRoot(root: NavRoot): Unit = Unit

    override fun replaceAllBackStacks(root: NavRoot): Unit = Unit

    override fun <T : Any> buildHostNavigation(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (BaseRoute, ComponentContext) -> T,
    ): Value<MultiStackHostState<T>> {
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
        key = "NoOpOverlaySlotKey",
        handleBackButton = true,
        childFactory = childFactory,
    )
}
