package com.thomaskioko.root.nav

import com.arkivanov.decompose.router.slot.SlotNavigation
import com.thomaskioko.root.model.EpisodeSheetConfig
import com.thomaskioko.root.model.ScreenSource

public interface EpisodeSheetNavigator {
    public fun showEpisodeSheet(episodeId: Long, source: ScreenSource)
    public fun dismissEpisodeSheet()
    public fun getSlotNavigation(): SlotNavigation<EpisodeSheetConfig>
}
