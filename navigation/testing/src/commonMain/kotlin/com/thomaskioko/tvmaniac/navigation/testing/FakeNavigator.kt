package com.thomaskioko.tvmaniac.navigation.testing

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
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
import com.thomaskioko.tvmaniac.navigation.OverlayRoute
import kotlin.reflect.KClass

/**
 * [Navigator] for tests that prefer property-style state inspection over [TestNavigator]'s event
 * flow. Mutations update internal state and are also recorded in public read-only properties.
 *
 * Use this when the test cares about end state (the active root, the last switched-to tab, the
 * last activated overlay) rather than the precise sequence of calls.
 *
 * Pass [initialActiveRoot] when the test observes [activeRoot] before any tab interaction.
 * Otherwise the fake reports [UnspecifiedNavRoot] until the first switch / show / replace call.
 */
public class FakeNavigator(
    initialActiveRoot: NavRoot = UnspecifiedNavRoot,
) : Navigator {

    private val overlayNavigation = SlotNavigation<NavRoute>()

    private val activeRootValue: MutableValue<NavRoot> = MutableValue(initialActiveRoot)
    private var _lastSwitchedTo: NavRoot? = null
    private var _lastShownRoot: NavRoot? = null
    private var _lastReplacedAllWith: NavRoot? = null
    private val _activatedOverlays = mutableListOf<NavRoute>()
    private val _navigatedRoutes = mutableListOf<NavRoute>()
    private var _navigateBackCount = 0

    override val activeRoot: Value<NavRoot> get() = activeRootValue

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
        }
    }

    override fun navigateBack() {
        _navigateBackCount++
    }

    override fun navigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean): Unit = Unit

    override fun popTo(toIndex: Int): Unit = Unit

    override fun bringToFront(route: NavRoute) {
        _navigatedRoutes += route
    }

    override fun pushToFront(route: NavRoute) {
        _navigatedRoutes += route
    }

    override fun switchBackStack(root: NavRoot) {
        _lastSwitchedTo = root
        activeRootValue.value = root
    }

    override fun showRoot(root: NavRoot) {
        _lastShownRoot = root
        activeRootValue.value = root
    }

    override fun replaceAllBackStacks(root: NavRoot) {
        _lastReplacedAllWith = root
        activeRootValue.value = root
    }

    override fun <T : Any> buildHostNavigation(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (BaseRoute, ComponentContext) -> T,
    ): Value<MultiStackHostState<T>> {
        activeRootValue.value = initialRoot
        val rootInstance = childFactory(initialRoot, componentContext)
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
        key = "FakeOverlaySlotKey",
        handleBackButton = true,
        childFactory = childFactory,
    )

    /** Resets recorded state for reuse across test cases. */
    public fun reset() {
        activeRootValue.value = UnspecifiedNavRoot
        _lastSwitchedTo = null
        _lastShownRoot = null
        _lastReplacedAllWith = null
        _activatedOverlays.clear()
        _navigatedRoutes.clear()
        _navigateBackCount = 0
        overlayNavigation.dismiss()
    }
}
