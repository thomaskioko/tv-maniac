package com.thomaskioko.tvmaniac.category.api.model

enum class Category(
    val id: Long,
    val title: String,
) {
    TRENDING(1, "Being Watched"),
    POPULAR(2, "Popular"),
    ANTICIPATED(3, "Anticipated"),
    TRENDING_TODAY(4, "Trending Today"),
}

fun Long.getCategory(): Category = when (this) {
    Category.POPULAR.id -> Category.POPULAR
    Category.TRENDING.id -> Category.TRENDING
    Category.ANTICIPATED.id -> Category.ANTICIPATED
    Category.TRENDING_TODAY.id -> Category.TRENDING_TODAY
    else -> Category.TRENDING
}
