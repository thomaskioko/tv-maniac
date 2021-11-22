package com.thomaskioko.tvmaniac.datasource.cache.genre

import com.thomaskioko.tvmaniac.datasource.cache.Genre

interface GenreCache {

    fun insert(genre: Genre)

    fun insert(genreList: List<Genre>)

    fun getGenreById(genreId: Int): Genre

    fun getGenres(): List<Genre>
}
