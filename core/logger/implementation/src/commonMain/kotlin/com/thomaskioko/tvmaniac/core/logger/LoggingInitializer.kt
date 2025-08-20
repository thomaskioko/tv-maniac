package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.Configs
import dev.zacsweers.metro.Inject

@Inject
class LoggingInitializer(
    private val logger: Logger,
    private val configs: Configs,
) : AppInitializer {

    override fun init() {
        logger.setup(configs.isDebug)
    }
}
