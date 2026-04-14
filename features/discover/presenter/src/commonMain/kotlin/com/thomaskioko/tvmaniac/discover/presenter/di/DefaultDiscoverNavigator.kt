package com.thomaskioko.tvmaniac.discover.presenter.di

import com.thomaskioko.root.model.ScreenSource
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.discover.nav.DiscoverNavigator
import com.thomaskioko.tvmaniac.home.nav.HomeTabNavigator
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.search.nav.SearchRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultDiscoverNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetNavigator: EpisodeSheetNavigator,
    private val homeTabNavigator: HomeTabNavigator,
) : DiscoverNavigator {
    override fun showDetails(traktId: Long) {
        rootNavigator.pushNew(ShowDetailsRoute(param = ShowDetailsParam(id = traktId)))
    }

    override fun showMoreShows(categoryId: Long) {
        rootNavigator.pushNew(MoreShowsRoute(categoryId))
    }

    override fun showSearch() {
        rootNavigator.pushNew(SearchRoute)
    }

    override fun showUpNext() {
        homeTabNavigator.switchToProgressTab()
    }

    override fun showEpisodeSheet(showTraktId: Long, episodeId: Long) {
        episodeSheetNavigator.showEpisodeSheet(episodeId, ScreenSource.DISCOVER)
    }

    override fun showSeason(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
        rootNavigator.pushNew(
            SeasonDetailsRoute(
                param = SeasonDetailsUiParam(
                    showTraktId = showTraktId,
                    seasonId = seasonId,
                    seasonNumber = seasonNumber,
                ),
            ),
        )
    }
}
