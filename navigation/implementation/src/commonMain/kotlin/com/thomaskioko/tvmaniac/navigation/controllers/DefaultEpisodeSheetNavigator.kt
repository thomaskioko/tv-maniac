package com.thomaskioko.tvmaniac.navigation.controllers

import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetConfig
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.SheetNavigator
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultEpisodeSheetNavigator(
    private val navigator: Navigator,
    private val sheetNavigator: SheetNavigator,
) : EpisodeSheetNavigator {

    override fun showEpisodeSheet(episodeId: Long, source: ScreenSource) {
        sheetNavigator.activate(EpisodeSheetConfig(episodeId = episodeId, source = source))
    }

    override fun dismissEpisodeSheet() {
        sheetNavigator.dismiss()
    }

    override fun dismissAndShowShowDetails(showTraktId: Long) {
        sheetNavigator.dismiss()
        navigator.pushToFront(ShowDetailsRoute(ShowDetailsParam(id = showTraktId)))
    }

    override fun dismissAndShowSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
        sheetNavigator.dismiss()
        navigator.pushNew(
            SeasonDetailsRoute(
                SeasonDetailsUiParam(
                    showTraktId = showTraktId,
                    seasonId = seasonId,
                    seasonNumber = seasonNumber,
                ),
            ),
        )
    }
}
