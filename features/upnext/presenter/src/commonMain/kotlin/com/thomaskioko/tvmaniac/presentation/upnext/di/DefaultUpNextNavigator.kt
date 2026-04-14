package com.thomaskioko.tvmaniac.presentation.upnext.di

import com.thomaskioko.root.model.ScreenSource
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.upnext.nav.UpNextNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultUpNextNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetNavigator: EpisodeSheetNavigator,
) : UpNextNavigator {
    override fun showDetails(traktId: Long) {
        rootNavigator.pushNew(ShowDetailsRoute(param = ShowDetailsParam(id = traktId)))
    }

    override fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
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

    override fun showEpisodeSheet(episodeId: Long) {
        episodeSheetNavigator.showEpisodeSheet(episodeId, ScreenSource.UP_NEXT)
    }
}
