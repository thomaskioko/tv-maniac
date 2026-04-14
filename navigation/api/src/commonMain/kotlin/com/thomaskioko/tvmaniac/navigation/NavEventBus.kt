package com.thomaskioko.tvmaniac.navigation

import kotlinx.coroutines.flow.SharedFlow

public sealed interface NavEvent {
    public data object ShowFollowed : NavEvent
}

public interface NavEventBus {
    public val events: SharedFlow<NavEvent>
    public fun emit(event: NavEvent)
}
