package com.thomaskioko.tvmaniac.testing.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@GraphExtension(ActivityScope::class)
@SingleIn(ActivityScope::class)
public interface TestActivityGraph {
    public val navDestinations: Set<NavDestination<*>>
    public val navRouteBindings: Set<NavRouteBinding<*>>

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createGraph(
            @Provides componentContext: ComponentContext,
        ): TestActivityGraph
    }
}
