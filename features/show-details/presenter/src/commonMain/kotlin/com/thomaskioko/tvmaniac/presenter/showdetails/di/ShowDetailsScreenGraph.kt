package com.thomaskioko.tvmaniac.presenter.showdetails.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.showdetails.nav.scope.ShowDetailsScreenScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(ShowDetailsScreenScope::class)
public interface ShowDetailsScreenGraph {
    public val showDetailsFactory: ShowDetailsPresenter.Factory

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createShowDetailsGraph(
            @Provides componentContext: ComponentContext,
        ): ShowDetailsScreenGraph
    }
}
