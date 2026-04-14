package com.thomaskioko.tvmaniac.navigation.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.root.GenreShowsDestination
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface GenreShowsNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideGenreShowsNavDestination(): NavDestination = object : NavDestination {
            override fun matches(config: RootDestinationConfig): Boolean =
                config is RootDestinationConfig.GenreShows

            override fun createChild(
                config: RootDestinationConfig,
                componentContext: ComponentContext,
            ): RootChild = GenreShowsDestination
        }
    }
}
