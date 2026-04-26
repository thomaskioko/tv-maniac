package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.logger.CompositeLogger
import com.thomaskioko.tvmaniac.core.logger.CrashReporter
import com.thomaskioko.tvmaniac.core.logger.FirebaseCrashLogger
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.logger.LoggingInitializerModule
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashReporter
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [
        CompositeLogger::class,
        KermitLogger::class,
        FirebaseCrashLogger::class,
        LoggingInitializerModule::class,
        FakeLogger::class,
        FakeCrashReporter::class,
    ],
)
public object TestLoggerBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideLogger(): Logger = FakeLogger()

    @Provides
    @SingleIn(AppScope::class)
    public fun provideCrashReporter(): CrashReporter = FakeCrashReporter()
}
