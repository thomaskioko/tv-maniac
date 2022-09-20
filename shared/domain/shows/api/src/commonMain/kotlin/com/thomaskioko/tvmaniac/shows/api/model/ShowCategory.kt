package com.thomaskioko.tvmaniac.shows.api.model

enum class ShowCategory(
    val type: Int,
    val title: String,
) {

    TRENDING(1, "Being Watched"),
    RECOMMENDED(2, "Recommended"),
    POPULAR(3, "Popular"),
    ANTICIPATED(4, "Anticipated"),
    FEATURED(5, "Featured");

    companion object {
        operator fun get(type: Int): ShowCategory {
            return when (type) {
                POPULAR.type -> POPULAR
                RECOMMENDED.type -> RECOMMENDED
                TRENDING.type -> TRENDING
                ANTICIPATED.type -> ANTICIPATED
                FEATURED.type -> FEATURED
                else -> RECOMMENDED
            }
        }
    }
}
