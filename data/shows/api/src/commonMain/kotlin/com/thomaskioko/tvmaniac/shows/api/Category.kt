package com.thomaskioko.tvmaniac.shows.api

enum class Category(
    val id: Long,
    val title: String,
) {
    TOP_RATED(1, "Top Rated"),
    POPULAR(2, "Popular"),
    UPCOMING(3, "Upcoming"),
    TRENDING_TODAY(4, "Trending Today"),
    FEATURED(5, "Featured"),
}
