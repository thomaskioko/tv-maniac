package com.thomaskioko.tvmaniac.navigation.navigators

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.EpisodeSheetController
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.presentation.episodedetail.ScreenSource
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultSeasonDetailsNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetController: EpisodeSheetController,
) : SeasonDetailsNavigator {
    override fun goBack() {
        rootNavigator.pop()
    }

    override fun showEpisodeSheet(episodeId: Long) {
        episodeSheetController.showEpisodeSheet(episodeId, ScreenSource.SEASON_DETAILS)
    }
}
