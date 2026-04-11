package com.thomaskioko.tvmaniac.core.logger

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object IosCrashReporterBindingContainer {

    @Provides
    public fun provideCrashReportingBridge(): CrashReportingBridge =
        CrashReportingBridgeHolder.bridge ?: NoOpCrashReportingBridge()
}
