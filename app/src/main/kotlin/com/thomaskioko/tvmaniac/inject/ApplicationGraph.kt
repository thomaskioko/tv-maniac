package com.thomaskioko.tvmaniac.inject

import android.app.Application
import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.AppInitializers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class, isExtendable = true)
interface ApplicationGraph {
    val initializers: AppInitializers

    @Multibinds
    fun appInitializers(): Set<AppInitializer>

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): ApplicationGraph
    }
}
