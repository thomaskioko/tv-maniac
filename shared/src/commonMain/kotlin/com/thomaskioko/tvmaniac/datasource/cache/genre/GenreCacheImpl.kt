package com.thomaskioko.tvmaniac.datasource.cache.genre

import com.thomaskioko.tvmaniac.datasource.cache.Genre
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

class GenreCacheImpl(
    private val database: TvManiacDatabase
) : GenreCache {

    private val genresQueries get() = database.genresQueries

    override fun insert(genre: Genre) {
        genresQueries.insertOrReplace(
            id = genre.id,
            name = genre.name
        )
    }

    override fun insert(genreList: List<Genre>) {
        genreList.forEach { insert(it) }
    }

    override fun getGenreById(genreId: Int): Genre {
        return genresQueries.selectById(genreId.toLong())
            .executeAsOne()
    }

    override fun getGenres(): List<Genre> {
        return genresQueries.selectAll()
            .executeAsList()
    }
}
