package com.thomaskioko.tvmaniac.genre.api

import com.thomaskioko.tvmaniac.core.db.Genre
import kotlinx.coroutines.flow.Flow

interface GenreCache {

    fun insert(genre: Genre)

    fun insert(genreList: List<Genre>)

    fun getGenreById(genreId: Int): Genre

    fun getGenres(): Flow<List<Genre>>
}
