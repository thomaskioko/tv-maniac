package com.thomaskioko.tvmaniac.datasource.enums


enum class TrendingDataRequest(val type: Int, val timeWindow: TimeWindow, val title: String) {

    FEATURED(1, TimeWindow.WEEK, "Featured"),
    TODAY(2, TimeWindow.DAY, "Trending Today"),
    THIS_WEEK(3, TimeWindow.WEEK, "Trending this week"),
    POPULAR(4, TimeWindow.WEEK,title ="Popular");

    companion object {
        operator fun get(type: Int): TrendingDataRequest {
            return when (type) {
                TODAY.type -> TODAY
                THIS_WEEK.type -> THIS_WEEK
                FEATURED.type -> FEATURED
                POPULAR.type -> POPULAR
                else -> THIS_WEEK
            }
        }
    }
}

