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
    public data class NavigateTo(val route: NavRoute) : NavEvent()
    public data class PushToFront(val route: NavRoute) : NavEvent()
    public data class BringToFront(val route: NavRoute) : NavEvent()
    public data object NavigateBack : NavEvent()
    public data class NavigateBackTo(val routeClass: KClass<out NavRoute>, val inclusive: Boolean) : NavEvent()
    public data class PopTo(val index: Int) : NavEvent()
    public data class SwitchBackStack(val root: NavRoot) : NavEvent()
    public data class ShowRoot(val root: NavRoot) : NavEvent()
    public data class ReplaceAllBackStacks(val root: NavRoot) : NavEvent()
}

/**
 * Alias used internally to check the class a [NavEvent.NavigateBackTo] or similar event was
 * constructed for.
 */
public typealias NavRouteClass = KClass<out NavRoute>
