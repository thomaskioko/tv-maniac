package com.thomaskioko.tvmaniac.genre.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.datasource.cache.Genre
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.genre.api.GenreCache
import kotlinx.coroutines.flow.Flow

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

    override fun getGenres(): Flow<List<Genre>> {
        return genresQueries.selectAll()
            .asFlow()
            .mapToList()
    }
}
