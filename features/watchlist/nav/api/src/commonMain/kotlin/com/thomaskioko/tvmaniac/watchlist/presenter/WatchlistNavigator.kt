package com.thomaskioko.tvmaniac.watchlist.presenter

public interface WatchlistNavigator {
    public fun showDetails(traktId: Long)
    public fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long)
}
