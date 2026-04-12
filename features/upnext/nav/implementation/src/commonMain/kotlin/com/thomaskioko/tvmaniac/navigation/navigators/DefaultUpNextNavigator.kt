package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.root.model.ScreenSource
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig.SeasonDetails
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig.ShowDetails
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsUiParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultUpNextNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetNavigator: EpisodeSheetNavigator,
) : UpNextNavigator {
    override fun showDetails(traktId: Long) {
        rootNavigator.pushNew(
            ShowDetails(param = ShowDetailsParam(id = traktId)),
        )
    }

    override fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
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

    override fun showEpisodeSheet(episodeId: Long) {
        episodeSheetNavigator.showEpisodeSheet(episodeId, ScreenSource.UP_NEXT)
    }
}
