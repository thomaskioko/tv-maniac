package com.thomaskioko.tvmaniac.navigation.controllers

import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.dismiss
import com.thomaskioko.root.model.EpisodeSheetConfig
import com.thomaskioko.root.model.ScreenSource
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultEpisodeSheetNavigator : EpisodeSheetNavigator {
    private val slotNavigation = SlotNavigation<EpisodeSheetConfig>()

    override fun showEpisodeSheet(episodeId: Long, source: ScreenSource) {
        slotNavigation.activate(EpisodeSheetConfig(episodeId = episodeId, source = source))
    }

    override fun dismissEpisodeSheet() {
        slotNavigation.dismiss()
    }

    override fun getSlotNavigation(): SlotNavigation<EpisodeSheetConfig> = slotNavigation
}
