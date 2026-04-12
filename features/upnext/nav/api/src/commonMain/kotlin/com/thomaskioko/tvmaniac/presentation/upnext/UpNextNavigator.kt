package com.thomaskioko.tvmaniac.presentation.upnext

public interface UpNextNavigator {
    public fun showDetails(traktId: Long)
    public fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long)
    public fun showEpisodeSheet(episodeId: Long)
}
