package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ScreenScope
import com.thomaskioko.tvmaniac.core.base.TabScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.progress.ProgressPresenter
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(TabScope::class)
public interface HomeTabGraph {
    public val discoverPresenter: DiscoverShowsPresenter
    public val progressPresenter: ProgressPresenter
    public val libraryPresenter: LibraryPresenter
    public val profilePresenter: ProfilePresenter

    @ContributesTo(ScreenScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createGraph(
            @Provides componentContext: ComponentContext,
        ): HomeTabGraph
    }
}
