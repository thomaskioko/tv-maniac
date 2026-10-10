package com.thomaskioko.tvmaniac.genreshows.nav.model

import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import kotlinx.serialization.Serializable

@Serializable
public data class GenreShowsParam(
    val slug: String,
    val name: String,
    val category: GenreShowCategory,
)
