package com.thomaskioko.tvmaniac.datasource.repository.genre

import com.thomaskioko.tvmaniac.datasource.cache.Genre
import com.thomaskioko.tvmaniac.datasource.cache.genre.GenreCache
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.network.model.GenresResponse
import com.thomaskioko.tvmaniac.datasource.repository.util.Resource
import com.thomaskioko.tvmaniac.datasource.repository.util.networkBoundResource
import com.thomaskioko.tvmaniac.util.Logger
import com.thomaskioko.tvmaniac.util.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class GenreRepositoryImpl(
    private val apiService: TvShowsService,
    private val genreCache: GenreCache,
    private val dispatcher: CoroutineDispatcher,
) : GenreRepository {

    override fun observeGenres(): Flow<Resource<List<Genre>>> = networkBoundResource(
        query = { genreCache.getGenres() },
        shouldFetch = { it.isNullOrEmpty() },
        fetch = { apiService.getAllGenres() },
        saveFetchResult = { mapAndCache(it) },
        onFetchFailed = { Logger("getGenres").log(it.getErrorMessage()) },
        coroutineDispatcher = dispatcher
    )

    private fun mapAndCache(it: GenresResponse) {
        val cacheList = it.genres.map { response ->
            Genre(
                id = response.id.toLong(),
                name = response.name
            )
        }
        genreCache.insert(cacheList)
    }
}
