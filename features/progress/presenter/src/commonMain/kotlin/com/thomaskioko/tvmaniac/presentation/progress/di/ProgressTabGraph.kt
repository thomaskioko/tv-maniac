package com.thomaskioko.tvmaniac.presentation.progress.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.presentation.progress.ProgressPresenter
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(ProgressRoot::class)
public interface ProgressTabGraph {
    public val progressPresenter: ProgressPresenter

    @ContributesTo(HomeRoute::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createProgressTabGraph(@Provides componentContext: ComponentContext): ProgressTabGraph
    }
}
