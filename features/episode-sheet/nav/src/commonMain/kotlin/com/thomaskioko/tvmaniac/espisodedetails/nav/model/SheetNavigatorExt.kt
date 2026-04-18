package com.thomaskioko.tvmaniac.espisodedetails.nav.model

import com.thomaskioko.tvmaniac.navigation.SheetNavigator

public fun SheetNavigator.showEpisodeSheet(episodeId: Long, source: ScreenSource) {
    activate(EpisodeSheetConfig(episodeId = episodeId, source = source))
}
