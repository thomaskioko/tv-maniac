package com.thomaskioko.tvmaniac.presenter.trailers.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.trailers.nav.scope.TrailersScreenScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(TrailersScreenScope::class)
public interface TrailersScreenGraph {
    public val trailersFactory: TrailersPresenter.Factory

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createTrailersGraph(
            @Provides componentContext: ComponentContext,
        ): TrailersScreenGraph
    }
}
