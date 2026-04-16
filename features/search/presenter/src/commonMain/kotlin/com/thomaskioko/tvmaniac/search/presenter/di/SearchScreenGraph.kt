package com.thomaskioko.tvmaniac.search.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.search.nav.scope.SearchScreenScope
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(SearchScreenScope::class)
public interface SearchScreenGraph {
    public val searchPresenter: SearchShowsPresenter

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createSearchGraph(
            @Provides componentContext: ComponentContext,
        ): SearchScreenGraph
    }
}
