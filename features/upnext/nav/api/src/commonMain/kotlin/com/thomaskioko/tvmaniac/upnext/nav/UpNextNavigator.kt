package com.thomaskioko.tvmaniac.upnext.nav

public interface UpNextNavigator {
    public fun showDetails(traktId: Long)
    public fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long)
    public fun showEpisodeSheet(episodeId: Long)
}
