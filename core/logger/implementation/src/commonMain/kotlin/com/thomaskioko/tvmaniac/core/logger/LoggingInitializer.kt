package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.Configs
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class LoggingInitializer(
    private val logger: Logger,
    private val configs: Configs,
) : AppInitializer {

    override fun init() {
        logger.setup(configs.isDebug)
    }
}
