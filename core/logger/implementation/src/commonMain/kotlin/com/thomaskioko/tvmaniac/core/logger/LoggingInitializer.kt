package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.util.api.BuildConfig
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class LoggingInitializer(
    private val logger: Logger,
) : AppInitializer {

    override fun init() {
        logger.setup(BuildConfig.IS_DEBUG)
    }
}
