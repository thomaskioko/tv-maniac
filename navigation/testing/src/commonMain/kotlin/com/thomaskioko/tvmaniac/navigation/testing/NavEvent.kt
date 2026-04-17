package com.thomaskioko.tvmaniac.navigation.testing

import com.thomaskioko.tvmaniac.navigation.NavRoute
import kotlin.reflect.KClass

/**
 * Captured navigation intent emitted by [TestNavigator] in the order each call was made.
 *
 * Tests consume these events through [NavigatorTurbine] and assert on the sequence and payload
 * of navigation operations, instead of stubbing feature-specific navigator interfaces by hand.
 */
public sealed class NavEvent {
    public data class PushNew(val route: NavRoute) : NavEvent()
    public data class PushToFront(val route: NavRoute) : NavEvent()
    public data class BringToFront(val route: NavRoute) : NavEvent()
    public data object Pop : NavEvent()
    public data class PopTo(val index: Int) : NavEvent()
    public data class GetStackNavigation(val instanceId: Int) : NavEvent()
}

/**
 * Alias used internally to check the class a [NavEvent.PopTo] or similar event was constructed for.
 */
public typealias NavRouteClass = KClass<out NavRoute>
