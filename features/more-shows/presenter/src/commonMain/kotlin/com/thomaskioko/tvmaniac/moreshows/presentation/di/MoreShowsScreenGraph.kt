package com.thomaskioko.tvmaniac.moreshows.presentation.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.moreshows.nav.scope.MoreShowsScreenScope
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(MoreShowsScreenScope::class)
public interface MoreShowsScreenGraph {
    public val moreShowsFactory: MoreShowsPresenter.Factory

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createMoreShowsGraph(
            @Provides componentContext: ComponentContext,
        ): MoreShowsScreenGraph
    }
}
