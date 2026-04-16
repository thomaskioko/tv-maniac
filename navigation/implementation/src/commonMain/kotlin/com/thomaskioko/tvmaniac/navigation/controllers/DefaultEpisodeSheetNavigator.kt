package com.thomaskioko.tvmaniac.navigation.controllers

import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.dismiss
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetConfig
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.SheetConfig
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
) : EpisodeSheetNavigator {
    private val slotNavigation = SlotNavigation<SheetConfig>()

    override fun showEpisodeSheet(episodeId: Long, source: ScreenSource) {
        slotNavigation.activate(EpisodeSheetConfig(episodeId = episodeId, source = source))
    }

    override fun dismissEpisodeSheet() {
        slotNavigation.dismiss()
    }

    override fun dismissAndShowShowDetails(showTraktId: Long) {
        slotNavigation.dismiss()
        navigator.pushToFront(ShowDetailsRoute(ShowDetailsParam(id = showTraktId)))
    }

    override fun dismissAndShowSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
        slotNavigation.dismiss()
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

    override fun getSlotNavigation(): SlotNavigation<SheetConfig> = slotNavigation
}
