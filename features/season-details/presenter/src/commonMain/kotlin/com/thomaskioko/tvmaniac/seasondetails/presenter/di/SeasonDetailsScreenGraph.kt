package com.thomaskioko.tvmaniac.seasondetails.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.seasondetails.nav.scope.SeasonDetailsScreenScope
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(SeasonDetailsScreenScope::class)
public interface SeasonDetailsScreenGraph {
    public val seasonDetailsFactory: SeasonDetailsPresenter.Factory

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createSeasonDetailsGraph(
            @Provides componentContext: ComponentContext,
        ): SeasonDetailsScreenGraph
    }
}
