package com.thomaskioko.tvmaniac.presentation.episodedetail.di

import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.espisodedetails.nav.EpisodeDetailNavigator
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultEpisodeDetailNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetNavigator: EpisodeSheetNavigator,
) : EpisodeDetailNavigator {
    override fun showDetails(showTraktId: Long) {
        episodeSheetNavigator.dismissEpisodeSheet()
        rootNavigator.pushToFront(ShowDetailsRoute(param = ShowDetailsParam(id = showTraktId)))
    }

    override fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
        episodeSheetNavigator.dismissEpisodeSheet()
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

    override fun dismiss() {
        episodeSheetNavigator.dismissEpisodeSheet()
    }
}
