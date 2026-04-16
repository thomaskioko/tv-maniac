package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultNavEventBus : NavEventBus {
    private val _events = MutableSharedFlow<NavEvent>(extraBufferCapacity = 1)

    override val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    override fun emit(event: NavEvent) {
        _events.tryEmit(event)
    }
}
