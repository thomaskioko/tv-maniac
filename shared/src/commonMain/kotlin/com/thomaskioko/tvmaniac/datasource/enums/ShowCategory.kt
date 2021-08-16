package com.thomaskioko.tvmaniac.datasource.enums


enum class ShowCategory(
    val type: Int,
    val title: String,
    val timeWindow: TimeWindow? = null
) {

    FEATURED(1, "Featured"),
    TODAY(2, "Trending Today", TimeWindow.DAY),
    THIS_WEEK(3, "Trending this week", TimeWindow.WEEK),
    TOP_RATED(4, "Top Rated"),
    TRENDING(5, "Popular"),
    POPULAR(6, "Popular");

    companion object {
        operator fun get(type: Int): ShowCategory {
            return when (type) {
                TODAY.type -> TODAY
                THIS_WEEK.type -> THIS_WEEK
                FEATURED.type -> FEATURED
                POPULAR.type -> POPULAR
                TOP_RATED.type -> TOP_RATED
                TRENDING.type -> TRENDING
                else -> THIS_WEEK
            }
        }
    }
}

