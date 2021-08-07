package com.thomaskioko.tvmaniac.datasource.enums


enum class TvShowType(val type: Int, val timeWindow: TimeWindow, val title: String) {

    FEATURED(1, TimeWindow.WEEK, "Featured"),
    TODAY(2, TimeWindow.DAY, "Trending Today"),
    THIS_WEEK(3, TimeWindow.WEEK, "Trending this week"),
    TOP_RATED(4, TimeWindow.WEEK, "Top Rated"),
    POPULAR(5, TimeWindow.WEEK, "Popular");

    companion object {
        operator fun get(type: Int): TvShowType {
            return when (type) {
                TODAY.type -> TODAY
                THIS_WEEK.type -> THIS_WEEK
                FEATURED.type -> FEATURED
                POPULAR.type -> POPULAR
                TOP_RATED.type -> TOP_RATED
                else -> THIS_WEEK
            }
        }
    }
}

