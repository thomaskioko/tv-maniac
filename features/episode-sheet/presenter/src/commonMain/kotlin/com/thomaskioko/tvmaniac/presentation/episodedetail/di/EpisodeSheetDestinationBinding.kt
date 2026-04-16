package com.thomaskioko.tvmaniac.presentation.episodedetail.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetConfig
import com.thomaskioko.tvmaniac.navigation.SheetChild
import com.thomaskioko.tvmaniac.navigation.SheetChildFactory
import com.thomaskioko.tvmaniac.navigation.SheetConfig
import com.thomaskioko.tvmaniac.navigation.SheetConfigBinding
import com.thomaskioko.tvmaniac.navigation.SheetDestination
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface EpisodeSheetDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideEpisodeSheetChildFactory(
            graphFactory: EpisodeSheetScreenGraph.Factory,
        ): SheetChildFactory = object : SheetChildFactory {
            override fun matches(config: SheetConfig): Boolean = config is EpisodeSheetConfig

            override fun createChild(
                config: SheetConfig,
                componentContext: ComponentContext,
            ): SheetChild {
                val sheetConfig = config as EpisodeSheetConfig
                return SheetDestination(
                    presenter = graphFactory.createEpisodeDetailGraph(componentContext)
                        .episodeDetailFactory.create(sheetConfig.episodeId, sheetConfig.source),
                )
            }
        }

        @Provides
        @IntoSet
        public fun provideEpisodeSheetConfigBinding(): SheetConfigBinding<*> =
            SheetConfigBinding(EpisodeSheetConfig::class, EpisodeSheetConfig.serializer())
    }
}
