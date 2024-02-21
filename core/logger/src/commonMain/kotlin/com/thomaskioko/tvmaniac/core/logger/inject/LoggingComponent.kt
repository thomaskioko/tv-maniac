package com.thomaskioko.tvmaniac.core.logger.inject

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.core.logger.LoggingInitializer
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface LoggingComponent {

  @IntoSet
  @Provides
  @ApplicationScope
  fun providesLoggingInitializer(bind: LoggingInitializer): AppInitializer = bind
}
