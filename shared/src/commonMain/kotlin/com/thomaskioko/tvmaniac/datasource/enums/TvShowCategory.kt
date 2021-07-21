package com.thomaskioko.tvmaniac.datasource.enums

enum class TvShowCategory(val type: Int, val title: String) {
    POPULAR_TV_SHOWS(1, "Popular Shows"),
    TOP_RATED_TV_SHOWS(2, "Top Rated Shows"),
    TRENDING(3, "Trending Shows");

    companion object {
        operator fun get(type: Int): TvShowCategory {
            return when (type) {
                POPULAR_TV_SHOWS.type -> POPULAR_TV_SHOWS
                TOP_RATED_TV_SHOWS.type -> TOP_RATED_TV_SHOWS
                TRENDING.type -> TRENDING
                else -> TOP_RATED_TV_SHOWS
            }
        }
    }
}