package com.thomaskioko.tvmaniac.initializers

import com.thomaskioko.tvmaniac.workmanager.AppInitializer
import javax.inject.Inject

class AppInitializers @Inject constructor(
    private val initializers: Set<@JvmSuppressWildcards AppInitializer>
) {
    fun init() {
        initializers.forEach {
            it.init()
        }
    }
}
