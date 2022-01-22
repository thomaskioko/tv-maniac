package com.thomaskioko.tvmaniac.seasons.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.remote.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.remote.util.getErrorMessage
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.implementation.mapper.toSeasonCacheList
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import com.thomaskioko.tvmaniac.shared.core.util.networkBoundResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SeasonsRepositoryImpl(
    private val apiService: TvShowsService,
    private val seasonCache: SeasonsCache,
    private val dispatcher: CoroutineDispatcher,
) : SeasonsRepository {

    override fun observeShowSeasons(tvShowId: Int): Flow<Resource<List<SelectSeasonsByShowId>>> =
        networkBoundResource(
            query = { seasonCache.observeSeasons(tvShowId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { apiService.getTvShowDetails(tvShowId) },
            saveFetchResult = { mapAndCache(it) },
            onFetchFailed = { Logger.withTag("observeShowSeasons").e(it.getErrorMessage()) },
            coroutineDispatcher = dispatcher
        )

    private fun mapAndCache(response: ShowDetailResponse) {
        val seasonsEntityList = response.toSeasonCacheList()

        val seasonIds = mutableListOf<Int>()
        for (season in seasonsEntityList) {
            seasonIds.add(season.id.toInt())
        }

        // Insert Seasons
        seasonCache.insert(seasonsEntityList)
    }
}
