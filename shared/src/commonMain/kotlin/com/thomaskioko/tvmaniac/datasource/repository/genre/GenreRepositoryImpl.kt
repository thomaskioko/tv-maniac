package com.thomaskioko.tvmaniac.datasource.repository.genre

import com.thomaskioko.tvmaniac.datasource.cache.Genre
import com.thomaskioko.tvmaniac.datasource.cache.genre.GenreCache
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.presentation.model.GenreModel

class GenreRepositoryImpl(
    private val apiService: TvShowsService,
    private val genreCache: GenreCache
) : GenreRepository {


    override suspend fun getGenres(): List<GenreModel> {
        return if (genreCache.getGenres().isEmpty()) {
            val response = apiService.getAllGenres()

            response.genres.forEach {
                genreCache.insert(
                    Genre(
                        id = it.id.toLong(),
                        name = it.name
                    )
                )
            }
            genreCache.getGenres().toGenreModelList()
        } else {
            genreCache.getGenres().toGenreModelList()
        }
    }


    private fun List<Genre>.toGenreModelList(): List<GenreModel> {
        return map {
            GenreModel(
                id = it.id.toInt(),
                name = it.name
            )
        }
    }

}