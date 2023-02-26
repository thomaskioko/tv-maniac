package com.thomaskioko.tvmaniac.category.api.model

enum class Category(
    val id: Long,
    val title: String,
) {

    TRENDING(1, "Being Watched"),
    POPULAR(3, "Popular"),
    ANTICIPATED(4, "Anticipated"),
    FEATURED(5, "Featured");

    companion object {
        operator fun get(type: Long): Category {
            return when (type) {
                POPULAR.id -> POPULAR
                TRENDING.id -> TRENDING
                ANTICIPATED.id -> ANTICIPATED
                FEATURED.id -> FEATURED
                else -> TRENDING
            }
        }
    }
}
