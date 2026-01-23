package com.thomaskioko.tvmaniac.resourcemanager.api

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

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
    WATCH_PROVIDERS(10, 6.days),
    GENRES(11, 7.days),
    GENRE_POSTER(12, 7.days),
    SHOWS_BY_GENRE(13, 3.days),
    WATCHLIST_METADATA(14, 1.days),
    USER_PROFILE(15, 3.days),
    FOLLOWED_SHOWS_SYNC(16, 3.hours),
    EPISODE_WATCHES_SYNC(17, 3.hours),
    SHOW_EPISODE_WATCHES_SYNC(18, 3.hours),
    SHOW_CAST(19, 6.days),
    TRAILERS(20, 6.days),
    UPCOMING_EPISODES(21, 3.hours),
    USER_STATS(22, 3.days),
    TRAKT_ACTIVITIES(23, 5.minutes),
}
