package com.thomaskioko.tvmaniac.navigation.testing

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import app.cash.turbine.testIn
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope

/**
 * Asserts on a sequence of navigation events recorded by [TestNavigator].
 *
 * Obtain one via [Navigator.test] (suspend block, auto-cancelling) or [Navigator.testIn]
 * (returns the turbine for manual control).
 */
public class NavigatorTurbine internal constructor(
    private val turbine: ReceiveTurbine<NavEvent>,
) {

    /** Suspends until the next event; fails if it is not [NavEvent.NavigateTo] with [route]. */
    public suspend fun awaitNavigateTo(route: NavRoute) {
        awaitTyped<NavEvent.NavigateTo>().let { event ->
            check(event.route == route) {
                "Expected NavigateTo(route=$route) but was NavigateTo(route=${event.route})"
            }
        }
    }

    /** Suspends until the next event; fails if it is not [NavEvent.PushToFront] with [route]. */
    public suspend fun awaitPushToFront(route: NavRoute) {
        awaitTyped<NavEvent.PushToFront>().let { event ->
            check(event.route == route) {
                "Expected PushToFront(route=$route) but was PushToFront(route=${event.route})"
            }
        }
    }

    /** Suspends until the next event; fails if it is not [NavEvent.BringToFront] with [route]. */
    public suspend fun awaitBringToFront(route: NavRoute) {
        awaitTyped<NavEvent.BringToFront>().let { event ->
            check(event.route == route) {
                "Expected BringToFront(route=$route) but was BringToFront(route=${event.route})"
            }
        }
    }

    /** Suspends until the next event; fails if it is not [NavEvent.NavigateBack]. */
    public suspend fun awaitNavigateBack() {
        awaitTyped<NavEvent.NavigateBack>()
    }

    /**
     * Suspends until the next event; fails if it is not [NavEvent.NavigateBackTo] with the
     * matching [routeClass] and [inclusive].
     */
    public suspend fun awaitNavigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean = false) {
        awaitTyped<NavEvent.NavigateBackTo>().let { event ->
            check(event.routeClass == routeClass && event.inclusive == inclusive) {
                "Expected NavigateBackTo(routeClass=$routeClass, inclusive=$inclusive) but was $event"
            }
        }
    }

    /** Type-safe overload of [awaitNavigateBackTo]. */
    public suspend inline fun <reified T : NavRoute> awaitNavigateBackTo(inclusive: Boolean = false) {
        awaitNavigateBackTo(T::class, inclusive)
    }

    /** Suspends until the next event; fails if it is not [NavEvent.PopTo] with [index]. */
    public suspend fun awaitPopTo(index: Int) {
        awaitTyped<NavEvent.PopTo>().let { event ->
            check(event.index == index) {
                "Expected PopTo(index=$index) but was PopTo(index=${event.index})"
            }
        }
    }

    /** Suspends until the next event; fails if it is not [NavEvent.SwitchBackStack] with [root]. */
    public suspend fun awaitSwitchBackStack(root: NavRoot) {
        awaitTyped<NavEvent.SwitchBackStack>().let { event ->
            check(event.root == root) {
                "Expected SwitchBackStack(root=$root) but was SwitchBackStack(root=${event.root})"
            }
        }
    }

    /** Suspends until the next event; fails if it is not [NavEvent.ShowRoot] with [root]. */
    public suspend fun awaitShowRoot(root: NavRoot) {
        awaitTyped<NavEvent.ShowRoot>().let { event ->
            check(event.root == root) {
                "Expected ShowRoot(root=$root) but was ShowRoot(root=${event.root})"
            }
        }
    }

    /** Suspends until the next event; fails if it is not [NavEvent.ReplaceAllBackStacks] with [root]. */
    public suspend fun awaitReplaceAllBackStacks(root: NavRoot) {
        awaitTyped<NavEvent.ReplaceAllBackStacks>().let { event ->
            check(event.root == root) {
                "Expected ReplaceAllBackStacks(root=$root) but was ReplaceAllBackStacks(root=${event.root})"
            }
        }
    }

    /** Suspends until the next event and returns it without asserting a variant. */
    public suspend fun awaitEvent(): NavEvent = turbine.awaitItem()

    /** Verifies there are no pending navigation events at this point. */
    public suspend fun expectNoEvents() {
        turbine.expectNoEvents()
    }

    /** Cancels the turbine, failing if there are unconsumed events. */
    public suspend fun cancel() {
        turbine.cancel()
    }

    /** Cancels the turbine and silently drops any unconsumed events. */
    public suspend fun cancelAndIgnoreRemainingEvents() {
        turbine.cancelAndIgnoreRemainingEvents()
    }

    @PublishedApi
    internal suspend fun awaitItem(): NavEvent = turbine.awaitItem()

    private suspend inline fun <reified T : NavEvent> awaitTyped(): T {
        val event = turbine.awaitItem()
        check(event is T) { "Expected ${T::class.simpleName} but was $event" }
        return event
    }
}

/**
 * Runs [block] with a [NavigatorTurbine] attached to this [Navigator]. The block must consume all
 * expected events; otherwise the test fails. The underlying Turbine collection is cancelled when
 * the block returns.
 *
 * Requires the receiver to be a [TestNavigator]; throws otherwise.
 */
public suspend fun Navigator.test(
    timeout: Duration? = null,
    block: suspend NavigatorTurbine.() -> Unit,
) {
    val navigator = this as? TestNavigator
        ?: error("Navigator.test requires a TestNavigator; was $this")
    navigator.events.test(timeout = timeout) {
        NavigatorTurbine(this).block()
    }
}

/**
 * Returns a [NavigatorTurbine] scoped to [scope] for manual start/stop control in longer-running
 * tests. Call [NavigatorTurbine.cancel] when finished.
 *
 * Requires the receiver to be a [TestNavigator]; throws otherwise.
 */
public fun Navigator.testIn(
    scope: CoroutineScope,
    timeout: Duration? = null,
): NavigatorTurbine {
    val navigator = this as? TestNavigator
        ?: error("Navigator.testIn requires a TestNavigator; was $this")
    return NavigatorTurbine(navigator.events.testIn(scope, timeout))
}
