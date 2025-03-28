package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.Configs
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
class LoggingInitializer(
  private val logger: Logger,
  private val configs: Configs
) : AppInitializer {

  override fun init() {
    logger.setup(configs.isDebug)
  }
}
