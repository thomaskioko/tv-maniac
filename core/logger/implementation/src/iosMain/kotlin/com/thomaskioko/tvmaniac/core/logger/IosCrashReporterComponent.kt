package com.thomaskioko.tvmaniac.core.logger

import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
public interface IosCrashReporterComponent {

    @Provides
    public fun provideCrashReportingBridge(): CrashReportingBridge =
        CrashReportingBridgeHolder.bridge ?: NoOpCrashReportingBridge()
}
