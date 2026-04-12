package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverNavigator
import com.thomaskioko.tvmaniac.navigation.EpisodeSheetController
import com.thomaskioko.tvmaniac.navigation.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.presentation.episodedetail.ScreenSource
import com.thomaskioko.tvmaniac.presenter.home.HomeTabController
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultDiscoverNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetController: EpisodeSheetController,
    private val homeTabController: HomeTabController,
) : DiscoverNavigator {
    override fun showDetails(traktId: Long) {
        rootNavigator.pushNew(
            RootDestinationConfig.ShowDetails(param = ShowDetailsParam(id = traktId)),
        )
    }

    override fun showMoreShows(categoryId: Long) {
        rootNavigator.pushNew(RootDestinationConfig.MoreShows(categoryId))
    }

    override fun showSearch() {
        rootNavigator.pushNew(RootDestinationConfig.Search)
    }

    override fun showUpNext() {
        homeTabController.switchToProgressTab()
    }

    override fun showEpisodeSheet(showTraktId: Long, episodeId: Long) {
        episodeSheetController.showEpisodeSheet(episodeId, ScreenSource.DISCOVER)
    }

    override fun showSeason(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
        rootNavigator.pushNew(
            RootDestinationConfig.SeasonDetails(
                param = SeasonDetailsUiParam(
                    showTraktId = showTraktId,
                    seasonId = seasonId,
                    seasonNumber = seasonNumber,
                ),
            ),
        )
    }
}
