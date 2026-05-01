package com.thomaskioko.tvmaniac.presentation.progress.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.home.nav.TabChild
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(HomeRoute::class)
public interface ProgressTabDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideProgressTabDestination(
            graphFactory: ProgressTabGraph.Factory,
        ): TabDestination = object : TabDestination {
            override fun matches(root: NavRoot): Boolean = root is ProgressRoot

            override fun createChild(
                root: NavRoot,
                componentContext: ComponentContext,
            ): TabChild<*> = TabChild(
                presenter = graphFactory.createProgressTabGraph(componentContext).progressPresenter,
            )
        }
    }
}
