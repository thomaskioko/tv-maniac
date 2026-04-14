package com.thomaskioko.tvmaniac.seasondetails.presenter.di

import com.thomaskioko.root.model.ScreenSource
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsNavigator
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(ActivityScope::class)
public class DefaultSeasonDetailsNavigator(
    private val rootNavigator: RootNavigator,
    private val episodeSheetNavigator: EpisodeSheetNavigator,
) : SeasonDetailsNavigator {
    override fun goBack() {
        rootNavigator.pop()
    }

    override fun showEpisodeSheet(episodeId: Long) {
        episodeSheetNavigator.showEpisodeSheet(episodeId, ScreenSource.SEASON_DETAILS)
    }
}
