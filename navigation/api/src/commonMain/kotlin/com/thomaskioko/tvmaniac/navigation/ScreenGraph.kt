package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.ScreenScope
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(ScreenScope::class)
public interface ScreenGraph {
    public val homePresenter: HomePresenter
    public val searchPresenter: SearchShowsPresenter
    public val settingsPresenter: SettingsPresenter
    public val debugPresenter: DebugPresenter

    public val showDetailsFactory: ShowDetailsPresenter.Factory
    public val seasonDetailsFactory: SeasonDetailsPresenter.Factory
    public val moreShowsFactory: MoreShowsPresenter.Factory
    public val trailersFactory: TrailersPresenter.Factory
    public val episodeDetailFactory: EpisodeDetailSheetPresenter.Factory

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createGraph(
            @Provides componentContext: ComponentContext,
        ): ScreenGraph
    }
}
