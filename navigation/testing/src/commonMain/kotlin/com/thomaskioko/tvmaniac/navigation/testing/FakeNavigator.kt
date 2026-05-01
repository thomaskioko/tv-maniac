package com.thomaskioko.tvmaniac.navigation.testing

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.navigation.BaseRoute
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.OverlayRoute
import kotlin.reflect.KClass

/**
 * [Navigator] for tests that prefer property-style state inspection over [TestNavigator]'s
 * event flow. Mutations apply to internal Decompose sources (so consumers wiring real children
 * still get valid stacks) and are also recorded in public read-only properties.
 *
 * Use this when the test cares about end state (the active root, the last switched-to tab, the
 * last activated overlay) rather than the precise sequence of calls.
 */
public class FakeNavigator : Navigator {

    private val rootNavigation = StackNavigation<NavRoot>()
    private val tabStacks = mutableMapOf<NavRoot, StackNavigation<BaseRoute>>()
    private val overlayNavigation = SlotNavigation<NavRoute>()

    private var _activeRoot: NavRoot? = null
    private var _lastSwitchedTo: NavRoot? = null
    private var _lastShownRoot: NavRoot? = null
    private var _lastReplacedAllWith: NavRoot? = null
    private val _activatedOverlays = mutableListOf<NavRoute>()
    private val _navigatedRoutes = mutableListOf<NavRoute>()
    private var _navigateBackCount = 0

    /** Most recently active [NavRoot]. Null until the first tab interaction. */
    public val activeRoot: NavRoot? get() = _activeRoot

    /** Most recent argument to [switchBackStack]. Null until the first call. */
    public val lastSwitchedTo: NavRoot? get() = _lastSwitchedTo

    /** Most recent argument to [showRoot]. Null until the first call. */
    public val lastShownRoot: NavRoot? get() = _lastShownRoot

    /** Most recent argument to [replaceAllBackStacks]. Null until the first call. */
    public val lastReplacedAllWith: NavRoot? get() = _lastReplacedAllWith

    /** Overlay routes activated through [navigateTo], in order. */
    public val activatedOverlays: List<NavRoute> get() = _activatedOverlays.toList()

    /** Most recent overlay route activated, or null if none. */
    public val lastActivatedOverlay: NavRoute? get() = _activatedOverlays.lastOrNull()

    /** Non-overlay routes pushed through [navigateTo], in order. */
    public val navigatedRoutes: List<NavRoute> get() = _navigatedRoutes.toList()

    /** Most recent non-overlay route pushed, or null if none. */
    public val lastNavigatedRoute: NavRoute? get() = _navigatedRoutes.lastOrNull()

    /** Number of times [navigateBack] has been invoked. */
    public val navigateBackCount: Int get() = _navigateBackCount

    override fun navigateTo(route: NavRoute) {
        if (route is OverlayRoute) {
            _activatedOverlays += route
            overlayNavigation.activate(route)
        } else {
            _navigatedRoutes += route
            activeStackOrNull()?.pushNew(route as BaseRoute)
        }
    }

    override fun navigateBack() {
        _navigateBackCount++
        activeStackOrNull()?.pop()
    }

    override fun navigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean) {
        activeStackOrNull()?.navigate(
            transformer = { stack ->
                val targetIndex = stack.indexOfLast { routeClass.isInstance(it) }
                if (targetIndex < 0) {
                    stack
                } else {
                    val keep = if (inclusive) targetIndex else targetIndex + 1
                    if (keep <= 0) stack else stack.take(keep)
                }
            },
            onComplete = { _, _ -> },
        )
    }

    override fun popTo(toIndex: Int) {
        activeStackOrNull()?.popTo(index = toIndex)
    }

    override fun bringToFront(route: NavRoute) {
        activeStackOrNull()?.bringToFront(route as BaseRoute)
    }

    override fun pushToFront(route: NavRoute) {
        activeStackOrNull()?.pushToFront(route as BaseRoute)
    }

    override fun switchBackStack(root: NavRoot) {
        _lastSwitchedTo = root
        _activeRoot = root
        rootNavigation.navigate(
            transformer = { stack ->
                val existing = stack.find { it::class == root::class }
                if (existing != null) {
                    stack.filterNot { it::class == root::class } + existing
                } else {
                    stack + root
                }
            },
            onComplete = { _, _ -> },
        )
    }

    override fun showRoot(root: NavRoot) {
        _lastShownRoot = root
        _activeRoot = root
        rootNavigation.navigate(
            transformer = { stack ->
                val existing = stack.find { it::class == root::class }
                if (existing != null) {
                    stack.filterNot { it::class == root::class } + existing
                } else {
                    stack + root
                }
            },
            onComplete = { _, _ -> },
        )
        tabStacks[root]?.navigate(
            transformer = { listOf(root as BaseRoute) },
            onComplete = { _, _ -> },
        )
    }

    override fun replaceAllBackStacks(root: NavRoot) {
        _lastReplacedAllWith = root
        _activeRoot = root
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
        if (_activeRoot == null) {
            _activeRoot = initialRoot
        }
        return componentContext.childStack(
            source = rootNavigation,
            serializer = null,
            initialConfiguration = initialRoot,
            key = "FakeRootTabStackKey",
            handleBackButton = false,
            childFactory = childFactory,
        )
    }

    override fun <T : Any> buildOverlaySlot(
        componentContext: ComponentContext,
        childFactory: (NavRoute, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>> = componentContext.childSlot(
        source = overlayNavigation,
        serializer = null,
        key = "FakeOverlaySlotKey",
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
            key = "FakeTabStack_${root::class.simpleName}",
            handleBackButton = true,
            childFactory = childFactory,
        )
    }

    /** Resets recorded state for reuse across test cases. Internal Decompose sources are kept. */
    public fun reset() {
        _activeRoot = null
        _lastSwitchedTo = null
        _lastShownRoot = null
        _lastReplacedAllWith = null
        _activatedOverlays.clear()
        _navigatedRoutes.clear()
        _navigateBackCount = 0
    }

    private fun activeStackOrNull(): StackNavigation<BaseRoute>? =
        _activeRoot?.let { tabStacks[it] }
}
