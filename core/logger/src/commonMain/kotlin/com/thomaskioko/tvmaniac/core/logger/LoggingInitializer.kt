package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.tatarka.inject.annotations.Inject

@Inject
class LoggingInitializer : AppInitializer {

  override fun init() {
    Napier.base(DebugAntilog())
  }
}
