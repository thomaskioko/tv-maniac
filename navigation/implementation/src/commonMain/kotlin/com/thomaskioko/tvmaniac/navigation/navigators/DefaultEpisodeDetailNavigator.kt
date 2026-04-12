package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.EpisodeSheetController
import com.thomaskioko.tvmaniac.navigation.RootDestinationConfig
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultEpisodeDetailNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetController: EpisodeSheetController,
) : EpisodeDetailNavigator {
    override fun showDetails(showTraktId: Long) {
        episodeSheetController.dismissEpisodeSheet()
        rootNavigator.pushToFront(
            RootDestinationConfig.ShowDetails(param = ShowDetailsParam(id = showTraktId)),
        )
    }

    override fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
        episodeSheetController.dismissEpisodeSheet()
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

    override fun dismiss() {
        episodeSheetController.dismissEpisodeSheet()
    }
}
