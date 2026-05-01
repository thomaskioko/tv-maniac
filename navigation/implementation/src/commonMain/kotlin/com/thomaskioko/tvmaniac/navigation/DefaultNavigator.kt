package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlin.reflect.KClass

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultNavigator(
    private val navRouteSerializer: NavRouteSerializer,
    private val navRootSerializer: NavRootSerializer,
    private val baseRouteSerializer: BaseRouteSerializer,
    private val navRoots: Set<NavRoot>,
) : Navigator {
    private val rootNavigation = StackNavigation<NavRoot>()
    private val tabStacks: Map<NavRoot, StackNavigation<BaseRoute>> =
        navRoots.associateWith { StackNavigation<BaseRoute>() }
    private val overlayNavigation = SlotNavigation<NavRoute>()

    private var activeRoot: NavRoot? = null

    override fun navigateTo(route: NavRoute) {
        if (route is OverlayRoute) {
            overlayNavigation.activate(route)
            return
        }
        activeTabStack().pushNew(route as BaseRoute)
    }

    override fun navigateBack() {
        activeTabStack().pop()
    }

    override fun navigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean) {
        activeTabStack().navigate(
            transformer = { stack -> popUntilTypeMatch(stack, routeClass, inclusive) },
            onComplete = { _, _ -> },
        )
    }

    override fun popTo(toIndex: Int) {
        activeTabStack().popTo(index = toIndex)
    }

    override fun bringToFront(route: NavRoute) {
        activeTabStack().bringToFront(route as BaseRoute)
    }

    override fun pushToFront(route: NavRoute) {
        activeTabStack().pushToFront(route as BaseRoute)
    }

    override fun switchBackStack(root: NavRoot) {
        requireRegistered(root)
        activeRoot = root
        rootNavigation.navigate(
            transformer = { stack -> moveOrPush(stack, root) },
            onComplete = { _, _ -> },
        )
    }

    override fun showRoot(root: NavRoot) {
        requireRegistered(root)
        activeRoot = root
        rootNavigation.navigate(
            transformer = { stack -> moveOrPush(stack, root) },
            onComplete = { _, _ -> },
        )
        tabStacks[root]?.navigate(
            transformer = { listOf(root as BaseRoute) },
            onComplete = { _, _ -> },
        )
    }

    override fun replaceAllBackStacks(root: NavRoot) {
        requireRegistered(root)
        activeRoot = root
        rootNavigation.navigate(
            transformer = { listOf(root) },
            onComplete = { _, _ -> },
        )
        tabStacks.forEach { (tabRoot, stack) ->
            stack.navigate(
                transformer = { listOf(tabRoot as BaseRoute) },
                onComplete = { _, _ -> },
            )
        }
    }

    override fun <T : Any> buildRootStack(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (NavRoot, ComponentContext) -> T,
    ): Value<ChildStack<*, T>> {
        requireRegistered(initialRoot)
        if (activeRoot == null) {
            activeRoot = initialRoot
        }
        return componentContext.childStack(
            source = rootNavigation,
            key = ROOT_TAB_STACK_KEY,
            initialConfiguration = initialRoot,
            serializer = navRootSerializer.serializer,
            handleBackButton = false,
            childFactory = childFactory,
        )
    }

    override fun <T : Any> buildOverlaySlot(
        componentContext: ComponentContext,
        childFactory: (NavRoute, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>> = componentContext.childSlot(
        source = overlayNavigation,
        key = OVERLAY_SLOT_KEY,
        serializer = navRouteSerializer.serializer,
        handleBackButton = true,
        childFactory = childFactory,
    )

    override fun <T : Any> buildTabStack(
        componentContext: ComponentContext,
        root: NavRoot,
        childFactory: (BaseRoute, ComponentContext) -> T,
    ): Value<ChildStack<*, T>> {
        val source = tabStacks[root]
            ?: error("NavRoot $root is not registered. Add it to Set<NavRoot>.")
        return componentContext.childStack(
            source = source,
            key = "TabStack_${root::class.simpleName}",
            initialConfiguration = root as BaseRoute,
            serializer = baseRouteSerializer.serializer,
            handleBackButton = true,
            childFactory = childFactory,
        )
    }

    private fun activeTabStack(): StackNavigation<BaseRoute> {
        val root = activeRoot
            ?: error("No active NavRoot. Call buildRootStack first to register the initial root.")
        return tabStacks[root]
            ?: error("NavRoot $root is not registered. Add it to Set<NavRoot>.")
    }

    private fun requireRegistered(root: NavRoot) {
        require(root in navRoots) {
            "NavRoot $root is not registered. Contribute it to Set<NavRoot> via @IntoSet."
        }
    }

    private fun moveOrPush(stack: List<NavRoot>, root: NavRoot): List<NavRoot> {
        val existing = stack.find { it::class == root::class }
        return if (existing != null) {
            stack.filterNot { it::class == root::class } + existing
        } else {
            stack + root
        }
    }

    private fun <C : Any> popUntilTypeMatch(
        stack: List<C>,
        routeClass: KClass<out NavRoute>,
        inclusive: Boolean,
    ): List<C> {
        val targetIndex = stack.indexOfLast { routeClass.isInstance(it) }
        if (targetIndex < 0) return stack
        val keep = if (inclusive) targetIndex else targetIndex + 1
        return if (keep <= 0) stack else stack.take(keep)
    }

    private companion object {
        const val ROOT_TAB_STACK_KEY = "RootTabStackKey"
        const val OVERLAY_SLOT_KEY = "OverlaySlotKey"
    }
}
