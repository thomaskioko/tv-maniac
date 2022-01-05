package com.thomaskioko.tvmaniac.datasource.repository.seasons

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.mapper.toSeasonCacheList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.datasource.repository.util.Resource
import com.thomaskioko.tvmaniac.datasource.repository.util.networkBoundResource
import com.thomaskioko.tvmaniac.util.Logger
import com.thomaskioko.tvmaniac.util.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SeasonsRepositoryImpl(
    private val apiService: TvShowsService,
    private val tvShowCache: TvShowCache,
    private val seasonCache: SeasonsCache,
    private val dispatcher: CoroutineDispatcher,
) : SeasonsRepository {

    override fun observeShowSeasons(tvShowId: Int): Flow<Resource<List<SelectSeasonsByShowId>>> =
        networkBoundResource(
            query = { seasonCache.observeSeasons(tvShowId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { apiService.getTvShowDetails(tvShowId) },
            saveFetchResult = { mapAndCache(it, tvShowId) },
            onFetchFailed = { Logger("observeShowSeasons").log(it.getErrorMessage()) },
            coroutineDispatcher = dispatcher
        )

    private fun mapAndCache(response: ShowDetailResponse, tvShowId: Int) {
        val seasonsEntityList = response.toSeasonCacheList()

        val seasonIds = mutableListOf<Int>()
        for (season in seasonsEntityList) {
            seasonIds.add(season.id.toInt())
        }

        tvShowCache.updateShowDetails(
            showId = tvShowId,
            showStatus = response.status,
            seasonIds = seasonIds
        )

        // Insert Seasons
        seasonCache.insert(seasonsEntityList)
    }
}
