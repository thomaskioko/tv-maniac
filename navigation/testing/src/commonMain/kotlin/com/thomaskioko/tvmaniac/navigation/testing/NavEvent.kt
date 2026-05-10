package com.thomaskioko.tvmaniac.navigation.testing

import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRoute
import kotlin.reflect.KClass

/**
 * Captured navigation intent emitted by [TestNavigator] in the order each call was made.
 *
 * Tests consume events through [NavigatorTurbine] and assert on the sequence and payload of
 * navigation operations, instead of stubbing feature-specific navigator interfaces by hand.
 */
public sealed class NavEvent {
    /**
     * Records one [com.thomaskioko.tvmaniac.navigation.Navigator.navigateTo] call.
     *
     * @property route route requested for push or overlay activation.
     */
    public data class NavigateTo(val route: NavRoute) : NavEvent()

    /**
     * Records one [com.thomaskioko.tvmaniac.navigation.Navigator.pushToFront] call.
     *
     * @property route route requested to move to (or land on) the top of the active stack.
     */
    public data class PushToFront(val route: NavRoute) : NavEvent()

    /**
     * Records one [com.thomaskioko.tvmaniac.navigation.Navigator.bringToFront] call.
     *
     * @property route route whose class was requested to move to (or reach) the top of the
     *   active stack.
     */
    public data class BringToFront(val route: NavRoute) : NavEvent()

    /** Records one [com.thomaskioko.tvmaniac.navigation.Navigator.navigateBack] call. */
    public data object NavigateBack : NavEvent()

    /**
     * Records one [com.thomaskioko.tvmaniac.navigation.Navigator.navigateBackTo] call.
     *
     * @property routeClass target type the navigator was asked to pop back to.
     * @property inclusive whether the matching entry was also requested for popping.
     */
    public data class NavigateBackTo(val routeClass: KClass<out NavRoute>, val inclusive: Boolean) : NavEvent()

    /**
     * Records one [com.thomaskioko.tvmaniac.navigation.Navigator.popTo] call.
     *
     * @property index target stack depth requested by the caller.
     */
    public data class PopTo(val index: Int) : NavEvent()

    /**
     * Records one [com.thomaskioko.tvmaniac.navigation.Navigator.switchBackStack] call.
     *
     * @property root tab the navigator was asked to switch to without resetting its stack.
     */
    public data class SwitchBackStack(val root: NavRoot) : NavEvent()

    /**
     * Records one [com.thomaskioko.tvmaniac.navigation.Navigator.showRoot] call.
     *
     * @property root tab the navigator was asked to switch to and reset to its root entry.
     */
    public data class ShowRoot(val root: NavRoot) : NavEvent()

    /**
     * Records one [com.thomaskioko.tvmaniac.navigation.Navigator.replaceAllBackStacks] call.
     *
     * @property root tab requested to remain active after every back stack is reset.
     */
    public data class ReplaceAllBackStacks(val root: NavRoot) : NavEvent()
}

/**
 * Alias used internally to check the class a [NavEvent.NavigateBackTo] or similar event was
 * constructed for.
 */
public typealias NavRouteClass = KClass<out NavRoute>
