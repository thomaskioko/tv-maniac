package com.thomaskioko.tvmaniac.datasource.enums

enum class ShowCategory(
    val type: Int,
    val title: String,
) {

    FEATURED(1, "Featured"),
    TRENDING(2, "Trending"),
    TOP_RATED(3, "Top Rated"),
    POPULAR(4, "Popular");

    companion object {
        operator fun get(type: Int): ShowCategory {
            return when (type) {
                FEATURED.type -> FEATURED
                POPULAR.type -> POPULAR
                TOP_RATED.type -> TOP_RATED
                TRENDING.type -> TRENDING
                else -> TOP_RATED
            }
        }
    }
}
