package com.thomaskioko.tvmaniac.discover.nav

public interface DiscoverNavigator {
    public fun showDetails(traktId: Long)
    public fun showMoreShows(categoryId: Long)
    public fun showSearch()
    public fun showUpNext()
    public fun showEpisodeSheet(showTraktId: Long, episodeId: Long)
    public fun showSeason(showTraktId: Long, seasonId: Long, seasonNumber: Long)
}
