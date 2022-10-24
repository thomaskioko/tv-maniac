package com.thomaskioko.tvmaniac.shows.api.model

enum class ShowCategory(
    val id: Int,
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
                POPULAR.id -> POPULAR
                RECOMMENDED.id -> RECOMMENDED
                TRENDING.id -> TRENDING
                ANTICIPATED.id -> ANTICIPATED
                FEATURED.id -> FEATURED
                else -> RECOMMENDED
            }
        }
    }
}
