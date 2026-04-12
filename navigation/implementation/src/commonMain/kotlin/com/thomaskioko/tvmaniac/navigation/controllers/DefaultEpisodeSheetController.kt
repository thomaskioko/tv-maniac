package com.thomaskioko.tvmaniac.navigation.controllers

import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.dismiss
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.EpisodeSheetConfig
import com.thomaskioko.tvmaniac.navigation.EpisodeSheetController
import com.thomaskioko.tvmaniac.navigation.model.ScreenSource
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultEpisodeSheetController : EpisodeSheetController {
    private val slotNavigation = SlotNavigation<EpisodeSheetConfig>()

    override fun showEpisodeSheet(episodeId: Long, source: ScreenSource) {
        slotNavigation.activate(EpisodeSheetConfig(episodeId = episodeId, source = source))
    }

    override fun dismissEpisodeSheet() {
        slotNavigation.dismiss()
    }

    override fun getSlotNavigation(): SlotNavigation<EpisodeSheetConfig> = slotNavigation
}
