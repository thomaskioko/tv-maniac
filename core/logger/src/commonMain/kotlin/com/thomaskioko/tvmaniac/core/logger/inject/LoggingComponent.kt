package com.thomaskioko.tvmaniac.core.logger.inject

import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface LoggingComponent {

  @Provides
  fun provideKermitLogger(
    configs: Configs,
  ): KermitLogger = KermitLogger(configs.isDebug)
}
