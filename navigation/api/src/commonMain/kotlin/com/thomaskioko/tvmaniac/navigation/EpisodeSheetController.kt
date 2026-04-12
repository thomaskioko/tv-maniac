package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.slot.SlotNavigation
import com.thomaskioko.tvmaniac.presentation.episodedetail.ScreenSource

public interface EpisodeSheetController {
    public fun showEpisodeSheet(episodeId: Long, source: ScreenSource)
    public fun dismissEpisodeSheet()
    public fun getSlotNavigation(): SlotNavigation<EpisodeSheetConfig>
}
