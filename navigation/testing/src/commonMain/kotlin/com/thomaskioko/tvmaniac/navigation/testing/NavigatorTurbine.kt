package com.thomaskioko.tvmaniac.navigation.testing

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import app.cash.turbine.testIn
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration

/**
 * Asserts on a sequence of navigation events recorded by [TestNavigator].
 *
 * Obtain one via [Navigator.test] (suspend block, auto-cancelling) or [Navigator.testIn]
 * (returns the turbine for manual control).
 */
public class NavigatorTurbine internal constructor(
    private val turbine: ReceiveTurbine<NavEvent>,
) {

    /** Suspends until the next event; fails if it is not [NavEvent.PushNew] with [route]. */
    public suspend fun awaitPushNew(route: NavRoute) {
        awaitTyped<NavEvent.PushNew>().let { event ->
            check(event.route == route) {
                "Expected PushNew(route=$route) but was PushNew(route=${event.route})"
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

    /** Suspends until the next event; fails if it is not [NavEvent.Pop]. */
    public suspend fun awaitPop() {
        awaitTyped<NavEvent.Pop>()
    }

    /** Suspends until the next event; fails if it is not [NavEvent.PopTo] with [index]. */
    public suspend fun awaitPopTo(index: Int) {
        awaitTyped<NavEvent.PopTo>().let { event ->
            check(event.index == index) {
                "Expected PopTo(index=$index) but was PopTo(index=${event.index})"
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
