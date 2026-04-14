package com.thomaskioko.tvmaniac.presentation.progress.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.TabChild
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import com.thomaskioko.tvmaniac.home.nav.scope.HomeScreenScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(HomeScreenScope::class)
public interface ProgressTabDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideProgressTabDestination(
            graphFactory: ProgressTabGraph.Factory,
        ): TabDestination = object : TabDestination {
            override fun matches(config: HomeConfig): Boolean =
                config is HomeConfig.Progress

            override fun createChild(
                config: HomeConfig,
                componentContext: ComponentContext,
            ): TabChild<*> = TabChild(
                presenter = graphFactory.createProgressTabGraph(componentContext).progressPresenter,
            )
        }
    }
}
