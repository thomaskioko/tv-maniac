package com.thomaskioko.tvmaniac.discover.api.model

enum class ShowCategory(
    val type: Int,
    val title: String,
) {

    TRENDING(1, "Trending"),
    TOP_RATED(2, "Top Rated"),
    POPULAR(3, "Popular");

    companion object {
        operator fun get(type: Int): ShowCategory {
            return when (type) {
                POPULAR.type -> POPULAR
                TOP_RATED.type -> TOP_RATED
                TRENDING.type -> TRENDING
                else -> TOP_RATED
            }
        }
    }
}
