package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.nav.model.SeasonDetailsUiParam
import com.thomaskioko.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.EpisodeSheetController
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailNavigator
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
