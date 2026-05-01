package com.thomaskioko.tvmaniac.navigation

import kotlin.reflect.KClass

internal sealed interface MultiStackNavEvent {
    data class Push(val route: NavRoute) : MultiStackNavEvent

    data class BringToFront(val route: NavRoute) : MultiStackNavEvent

    data class PushToFront(val route: NavRoute) : MultiStackNavEvent

    data class Pop(val toIndex: Int = -1) : MultiStackNavEvent

    data class PopUntilType(
        val routeClass: KClass<out NavRoute>,
        val inclusive: Boolean,
    ) : MultiStackNavEvent

    data class SwitchTab(val root: NavRoot, val resetStack: Boolean) : MultiStackNavEvent

    data class ReplaceAll(val root: NavRoot) : MultiStackNavEvent
}
