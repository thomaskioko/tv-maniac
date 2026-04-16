package com.thomaskioko.root.nav

import com.arkivanov.decompose.router.slot.SlotNavigation
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.navigation.SheetConfig

public interface EpisodeSheetNavigator {
    public fun showEpisodeSheet(episodeId: Long, source: ScreenSource)
    public fun dismissEpisodeSheet()
    public fun dismissAndShowShowDetails(showTraktId: Long)
    public fun dismissAndShowSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long)
    public fun getSlotNavigation(): SlotNavigation<SheetConfig>
}
