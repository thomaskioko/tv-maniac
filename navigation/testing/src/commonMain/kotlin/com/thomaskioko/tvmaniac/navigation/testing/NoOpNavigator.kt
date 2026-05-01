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

/**
 * [Navigator] that ignores every navigation call. Use in presenter tests where the test does not
 * assert on navigation events (only on state, side effects, etc.). Tests that want to verify
 * navigation calls should use [TestNavigator] instead.
 *
 * Each builder returns a real `Value<ChildStack>` or `Value<ChildSlot>` backed by an internal
 * Decompose source so tests that wire a real Decompose child against this navigator still get a
 * valid stack. No mutation calls are recorded; stacks stay at their initial entries.
 */
public class NoOpNavigator : Navigator {

    private val rootNavigation = StackNavigation<NavRoot>()
    private val tabStacks = mutableMapOf<NavRoot, StackNavigation<BaseRoute>>()
    private val overlayNavigation = SlotNavigation<NavRoute>()

    override fun navigateTo(route: NavRoute): Unit = Unit

    override fun navigateBack(): Unit = Unit

    override fun navigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean): Unit = Unit

    override fun popTo(toIndex: Int): Unit = Unit

    override fun bringToFront(route: NavRoute): Unit = Unit

    override fun pushToFront(route: NavRoute): Unit = Unit

    override fun switchBackStack(root: NavRoot): Unit = Unit

    override fun showRoot(root: NavRoot): Unit = Unit

    override fun replaceAllBackStacks(root: NavRoot): Unit = Unit

    override fun <T : Any> buildRootStack(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (NavRoot, ComponentContext) -> T,
    ): Value<ChildStack<*, T>> = componentContext.childStack(
        source = rootNavigation,
        serializer = null,
        initialConfiguration = initialRoot,
        key = "NoOpRootTabStackKey",
        handleBackButton = false,
        childFactory = childFactory,
    )

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
            key = "NoOpTabStack_${root::class.simpleName}",
            handleBackButton = true,
            childFactory = childFactory,
        )
    }
}
