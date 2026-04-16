package com.thomaskioko.tvmaniac.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class FakeNavEventBus : NavEventBus {
    private val _events = MutableSharedFlow<NavEvent>(extraBufferCapacity = 16)
    private val _emitted = mutableListOf<NavEvent>()

    override val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    val emitted: List<NavEvent> get() = _emitted.toList()

    override fun emit(event: NavEvent) {
        _emitted += event
        _events.tryEmit(event)
    }
}
