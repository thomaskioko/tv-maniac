package com.thomaskioko.tvmaniac.genre.model

public data class GenreShowsStoreKey(
    val genreSlug: String,
    val category: GenreShowCategory = GenreShowCategory.POPULAR,
)
