package com.thomaskioko.tvmaniac.domain.telemetry.di

import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.Initializers
import com.thomaskioko.tvmaniac.domain.telemetry.TelemetryContextInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public interface TelemetryContextInitializerBindingContainer {
    public companion object {
        @Provides
        @IntoSet
        @Initializers
        public fun provideTelemetryContextInitializer(
            bind: TelemetryContextInitializer,
        ): Initializer = Initializer { bind.init() }
    }
}
