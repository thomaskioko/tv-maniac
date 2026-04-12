package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig.SeasonDetails
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig.ShowDetails
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsUiParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultEpisodeDetailNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetNavigator: EpisodeSheetNavigator,
) : EpisodeDetailNavigator {
    override fun showDetails(showTraktId: Long) {
        episodeSheetNavigator.dismissEpisodeSheet()
        rootNavigator.pushToFront(
            ShowDetails(param = ShowDetailsParam(id = showTraktId)),
        )
    }

    override fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
        episodeSheetNavigator.dismissEpisodeSheet()
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

    override fun dismiss() {
        episodeSheetNavigator.dismissEpisodeSheet()
    }
}
