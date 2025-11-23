package com.thomaskioko.tvmaniac.resourcemanager.api

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

enum class RequestTypeConfig(val requestId: Long, val duration: Duration) {
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
}
