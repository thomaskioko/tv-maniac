package com.thomaskioko.tvmaniac.initializers

import com.thomaskioko.tvmaniac.util.AppInitializer
import me.tatarka.inject.annotations.Inject

@Inject
class AppInitializers(
    private val initializers: Set<AppInitializer>,
) {
    fun init() {
        for (initializer in initializers) {
            initializer.init()
        }
    }
}
