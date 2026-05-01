package com.thomaskioko.tvmaniac.presentation.library.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.home.nav.TabChild
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.library.nav.LibraryRoot
import com.thomaskioko.tvmaniac.navigation.NavRoot
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(HomeRoute::class)
public interface LibraryTabDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideLibraryTabDestination(
            graphFactory: LibraryTabGraph.Factory,
        ): TabDestination = object : TabDestination {
            override fun matches(root: NavRoot): Boolean = root is LibraryRoot

            override fun createChild(
                root: NavRoot,
                componentContext: ComponentContext,
            ): TabChild<*> = TabChild(
                presenter = graphFactory.createLibraryTabGraph(componentContext).libraryPresenter,
            )
        }
    }
}
