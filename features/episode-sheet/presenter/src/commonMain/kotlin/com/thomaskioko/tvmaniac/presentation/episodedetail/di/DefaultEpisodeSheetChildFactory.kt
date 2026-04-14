package com.thomaskioko.tvmaniac.presentation.episodedetail.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.root.model.EpisodeSheetConfig
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.SheetChild
import com.thomaskioko.tvmaniac.navigation.SheetDestination
import com.thomaskioko.tvmaniac.navigation.root.EpisodeSheetChildFactory
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultEpisodeSheetChildFactory(
    private val graphFactory: EpisodeSheetScreenGraph.Factory,
) : EpisodeSheetChildFactory {
    override fun createChild(config: EpisodeSheetConfig, componentContext: ComponentContext): SheetChild =
        SheetDestination(
            presenter = graphFactory.createEpisodeDetailGraph(componentContext)
                .episodeDetailFactory.create(config.episodeId, config.source),
        )
}
