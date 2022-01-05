package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Genre
import com.thomaskioko.tvmaniac.presentation.model.GenreUIModel

fun List<Genre>.toGenreModelList(): List<GenreUIModel> {
    return map {
        GenreUIModel(
            id = it.id.toInt(),
            name = it.name
        )
    }
}
