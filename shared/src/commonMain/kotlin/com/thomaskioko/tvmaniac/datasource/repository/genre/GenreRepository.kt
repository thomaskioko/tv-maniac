package com.thomaskioko.tvmaniac.datasource.repository.genre

import com.thomaskioko.tvmaniac.presentation.model.GenreModel

interface GenreRepository  {

    suspend fun getGenres() : List<GenreModel>

}