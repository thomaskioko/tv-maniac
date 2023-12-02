package com.thomaskioko.tvmaniac.util.inject

import com.thomaskioko.tvmaniac.util.AppInitializer
import com.thomaskioko.tvmaniac.util.logging.LoggingInitializer
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface LoggingComponent {

    @IntoSet
    @Provides
    @ApplicationScope
    fun providesLoggingInitializer(bind: LoggingInitializer): AppInitializer = bind
}
