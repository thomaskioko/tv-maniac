package com.thomaskioko.tvmaniac.shared

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.AppInitializers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
interface IosApplicationGraph {
    val initializers: AppInitializers

    @Multibinds(allowEmpty = true)
    fun appInitializers(): Set<AppInitializer>
}

fun createIosApplicationComponent(): IosApplicationGraph {
    return createGraph<IosApplicationGraph>()
}
