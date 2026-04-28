package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.util.DateTimeBindingContainer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlin.time.Clock
import kotlin.time.Instant

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [DateTimeBindingContainer::class],
)
public object TestDateTimeBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideClock(): Clock = object : Clock {
        private val fixedInstant = Instant.parse("2026-04-19T00:00:00Z")
        override fun now(): Instant = fixedInstant
    }
}
