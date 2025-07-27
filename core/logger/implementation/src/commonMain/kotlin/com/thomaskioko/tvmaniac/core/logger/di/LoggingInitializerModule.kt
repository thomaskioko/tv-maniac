package com.thomaskioko.tvmaniac.core.logger.di

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.logger.LoggingInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface LoggingInitializerModule {
    @Provides
    @IntoSet
    fun provideLoggingInitializer(impl: LoggingInitializer): AppInitializer = impl
}
