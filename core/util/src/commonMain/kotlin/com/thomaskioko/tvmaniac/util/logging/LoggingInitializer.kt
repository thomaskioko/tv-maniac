package com.thomaskioko.tvmaniac.util.logging

import com.thomaskioko.tvmaniac.util.AppInitializer
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.Configs
import me.tatarka.inject.annotations.Inject

@Inject
class LoggingInitializer(private val configs: Configs) : AppInitializer {

    override fun init() {
        KermitLogger(configs)
    }
}
