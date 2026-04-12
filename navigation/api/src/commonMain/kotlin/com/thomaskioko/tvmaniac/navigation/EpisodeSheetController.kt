package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.slot.SlotNavigation
import com.thomaskioko.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.navigation.model.EpisodeSheetConfig

public interface EpisodeSheetController {
    public fun showEpisodeSheet(episodeId: Long, source: ScreenSource)
    public fun dismissEpisodeSheet()
    public fun getSlotNavigation(): SlotNavigation<EpisodeSheetConfig>
}
