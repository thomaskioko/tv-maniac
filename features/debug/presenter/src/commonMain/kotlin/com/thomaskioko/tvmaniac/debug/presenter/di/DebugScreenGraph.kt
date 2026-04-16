package com.thomaskioko.tvmaniac.debug.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.debug.nav.DebugScreenScope
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(DebugScreenScope::class)
public interface DebugScreenGraph {
    public val debugPresenter: DebugPresenter

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createDebugGraph(
            @Provides componentContext: ComponentContext,
        ): DebugScreenGraph
    }
}
