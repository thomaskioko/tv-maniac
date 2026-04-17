package com.thomaskioko.tvmaniac.navigation.testing

import com.arkivanov.decompose.router.stack.StackNavigation
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * [Navigator] implementation for unit tests that records every call as a [NavEvent] and exposes
 * them via [events]. No real stack mutation happens; feature-specific navigators that delegate to
 * [Navigator] can be wired directly to this fake to assert on the routes they push.
 *
 * Consume events through [Navigator.test] or [Navigator.testIn] rather than subscribing to
 * [events] directly so that unconsumed events fail the enclosing test.
 */
public class TestNavigator : Navigator {

    private val _events = MutableSharedFlow<NavEvent>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    /** All navigation calls recorded since this navigator was created, in order. */
    public val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    private val stackNavigation = StackNavigation<NavRoute>()

    override fun bringToFront(route: NavRoute) {
        _events.tryEmit(NavEvent.BringToFront(route))
    }

    override fun pushNew(route: NavRoute) {
        _events.tryEmit(NavEvent.PushNew(route))
    }

    override fun pushToFront(route: NavRoute) {
        _events.tryEmit(NavEvent.PushToFront(route))
    }

    override fun pop() {
        _events.tryEmit(NavEvent.Pop)
    }

    override fun popTo(toIndex: Int) {
        _events.tryEmit(NavEvent.PopTo(toIndex))
    }

    override fun getStackNavigation(): StackNavigation<NavRoute> = stackNavigation
}
