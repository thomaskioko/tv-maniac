package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.root.model.ScreenSource
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverNavigator
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig.SeasonDetails
import com.thomaskioko.tvmaniac.presenter.home.HomeTabNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsUiParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultDiscoverNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetNavigator: EpisodeSheetNavigator,
    private val homeTabNavigator: HomeTabNavigator,
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
        homeTabNavigator.switchToProgressTab()
    }

    override fun showEpisodeSheet(showTraktId: Long, episodeId: Long) {
        episodeSheetNavigator.showEpisodeSheet(episodeId, ScreenSource.DISCOVER)
    }

    override fun showSeason(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
        rootNavigator.pushNew(
            SeasonDetails(
                param = SeasonDetailsUiParam(
                    showTraktId = showTraktId,
                    seasonId = seasonId,
                    seasonNumber = seasonNumber,
                ),
            ),
        )
    }
}
