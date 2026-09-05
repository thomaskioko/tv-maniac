package com.thomaskioko.tvmaniac.core.logger

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.Initializers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@Inject
public class CrashlyticsInitializer {

    public fun init() {
        enableCrashlytics()
        setCrashlyticsUnhandledExceptionHook()
    }
}

@ContributesTo(AppScope::class)
public interface CrashlyticsInitializerModule {
    public companion object {
        @Provides
        @IntoSet
        @Initializers
        public fun provideCrashlyticsInitializer(bind: CrashlyticsInitializer): Initializer = Initializer { bind.init() }
    }
}
