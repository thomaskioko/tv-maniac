package com.thomaskioko.tvmaniac.presenter.home.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.home.nav.scope.HomeScreenScope
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(HomeScreenScope::class)
public interface HomeScreenGraph {
    public val homePresenter: HomePresenter

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createHomeGraph(
            @Provides componentContext: ComponentContext,
        ): HomeScreenGraph
    }
}
