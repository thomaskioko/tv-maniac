package com.thomaskioko.tvmaniac.category.api.model

enum class Category(
    val id: Long,
    val title: String,
) {
    TRENDING(1, "Being Watched"),
    POPULAR(3, "Popular"),
    ANTICIPATED(4, "Anticipated"),
    FEATURED(5, "Featured"),
}

fun Long.getCategory(): Category = when (this) {
    Category.POPULAR.id -> Category.POPULAR
    Category.TRENDING.id -> Category.TRENDING
    Category.ANTICIPATED.id -> Category.ANTICIPATED
    Category.FEATURED.id -> Category.FEATURED
    else -> Category.TRENDING
}
