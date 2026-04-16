package com.thomaskioko.root.nav

import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource

public interface EpisodeSheetNavigator {
    public fun showEpisodeSheet(episodeId: Long, source: ScreenSource)
    public fun dismissEpisodeSheet()
    public fun dismissAndShowShowDetails(showTraktId: Long)
    public fun dismissAndShowSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long)
}
