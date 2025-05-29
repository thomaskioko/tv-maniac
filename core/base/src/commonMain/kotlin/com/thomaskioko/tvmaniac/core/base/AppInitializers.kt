package com.thomaskioko.tvmaniac.core.base

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

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
