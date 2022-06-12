package com.thomaskioko.tvmaniac.genre.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.datasource.cache.Genre
import com.thomaskioko.tvmaniac.genre.api.GenreCache
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.remote.api.model.GenresResponse
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
        onFetchFailed = { Logger.withTag("observeGenres").e(it.resolveError()) },
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
