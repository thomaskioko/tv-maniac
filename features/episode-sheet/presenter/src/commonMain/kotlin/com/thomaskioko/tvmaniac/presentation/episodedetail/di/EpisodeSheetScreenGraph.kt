package com.thomaskioko.tvmaniac.presentation.episodedetail.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.espisodedetails.nav.scope.EpisodeSheetScreenScope
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(EpisodeSheetScreenScope::class)
public interface EpisodeSheetScreenGraph {
    public val episodeDetailFactory: EpisodeSheetPresenter.Factory

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createEpisodeDetailGraph(
            @Provides componentContext: ComponentContext,
        ): EpisodeSheetScreenGraph
    }
}
