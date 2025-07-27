package com.thomaskioko.tvmaniac.core.base

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
class AppInitializers(
    private val initializers: Set<AppInitializer>,
) {
    fun initialize() {
        for (initializer in initializers) {
            initializer.init()
        }
    }
}
