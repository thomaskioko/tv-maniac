package com.thomaskioko.tvmaniac.util

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlin.time.Clock

@BindingContainer
@ContributesTo(AppScope::class)
public object DateTimeBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideClock(): Clock = Clock.System
}
