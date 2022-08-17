package com.thomaskioko.tvmaniac.seasons.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.implementation.mapper.toSeasonCacheList
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SeasonsRepositoryImpl(
    private val apiService: TmdbService,
    private val seasonCache: SeasonsCache,
    private val dispatcher: CoroutineDispatcher,
) : SeasonsRepository {

    override fun observeShowSeasons(tvShowId: Long): Flow<Resource<List<SelectSeasonsByShowId>>> =
        networkBoundResource(
            query = { seasonCache.observeSeasons(tvShowId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { apiService.getTvShowDetails(tvShowId) },
            saveFetchResult = { seasonCache.insert(it.toSeasonCacheList()) },
            onFetchFailed = { Logger.withTag("observeShowSeasons").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )
}
