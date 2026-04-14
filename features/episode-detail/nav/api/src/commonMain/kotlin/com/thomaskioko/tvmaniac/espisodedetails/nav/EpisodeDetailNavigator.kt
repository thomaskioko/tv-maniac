package com.thomaskioko.tvmaniac.espisodedetails.nav

public interface EpisodeDetailNavigator {
    public fun showDetails(showTraktId: Long)
    public fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long)
    public fun dismiss()
}
