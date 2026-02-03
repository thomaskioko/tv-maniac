package com.thomaskioko.tvmaniac.resourcemanager.api

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

public enum class RequestTypeConfig(public val requestId: Long, public val duration: Duration) {
    FEATURED_SHOWS_TODAY(1, 1.days),
    POPULAR_SHOWS(2, 6.days),
    RECOMMENDED_SHOWS(3, 6.days),
    SEASON_DETAILS(4, 5.days),
    SHOW_DETAILS(5, 6.days),
    SIMILAR_SHOWS(6, 6.days),
    TOP_RATED_SHOWS(7, 3.days),
    TRENDING_SHOWS_TODAY(8, 1.days),
    UPCOMING_SHOWS(9, 3.days),
    WATCHLIST_METADATA(14, 1.days),
    USER_PROFILE(15, 3.days),
    WATCHLIST_SYNC(16, 1.days),
    EPISODE_WATCHES_SYNC(17, 1.days),
    SHOW_EPISODE_WATCHES_SYNC(18, 1.hours),
    SHOW_CAST(19, 6.days),
    TRAILERS(20, 6.days),
    UPCOMING_EPISODES(21, 5.days),
    USER_STATS(22, 3.days),
    TRAKT_ACTIVITIES(23, 1.days),
    SEASONS_EPISODES_SYNC(24, 3.days),
    SHOW_SEASON_DETAILS_SYNC(25, 3.hours),
}
