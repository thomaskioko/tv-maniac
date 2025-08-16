package com.thomaskioko.tvmaniac.core.logger.di

import com.thomaskioko.tvmaniac.core.base.di.Initializers
import com.thomaskioko.tvmaniac.core.logger.LoggingInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface LoggingInitializerModule {
    @Provides
    @IntoSet
    @Initializers
    fun provideLoggingInitializer(impl: LoggingInitializer): () -> Unit = {
        impl.init()
    }
}
