package com.thomaskioko.tvmaniac.presentation.episodedetail.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.espisodedetails.nav.scope.EpisodeDetailScreenScope
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(EpisodeDetailScreenScope::class)
public interface EpisodeDetailScreenGraph {
    public val episodeDetailFactory: EpisodeDetailSheetPresenter.Factory

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createEpisodeDetailGraph(
            @Provides componentContext: ComponentContext,
        ): EpisodeDetailScreenGraph
    }
}
