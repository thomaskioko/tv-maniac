package com.thomaskioko.tvmaniac.presentation.library.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.TabChild
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import com.thomaskioko.tvmaniac.home.nav.scope.HomeScreenScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(HomeScreenScope::class)
public interface LibraryTabDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideLibraryTabDestination(
            graphFactory: LibraryTabGraph.Factory,
        ): TabDestination = object : TabDestination {
            override fun matches(config: HomeConfig): Boolean =
                config is HomeConfig.Library

            override fun createChild(
                config: HomeConfig,
                componentContext: ComponentContext,
            ): TabChild<*> = TabChild(
                presenter = graphFactory.createLibraryTabGraph(componentContext).libraryPresenter,
            )
        }
    }
}
