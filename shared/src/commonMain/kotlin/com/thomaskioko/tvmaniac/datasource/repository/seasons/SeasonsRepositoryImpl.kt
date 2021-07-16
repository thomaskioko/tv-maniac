package com.thomaskioko.tvmaniac.datasource.repository.seasons

import com.thomaskioko.tvmaniac.datasource.cache.db.TvShowCache
import com.thomaskioko.tvmaniac.datasource.cache.db.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toSeasonsEntityList
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService

class SeasonsRepositoryImpl(
    private val apiService: TvShowsService,
    private val tvShowCache: TvShowCache,
    private val seasonCache: SeasonsCache
) : SeasonsRepository {


    override suspend fun getSeasonListByTvShowId(tvShowId: Int): List<SeasonsEntity> {
        return if (seasonCache.getSeasonListByTvShowId(tvShowId).isEmpty()) {

            updateTvShowsDetails(tvShowId)

            seasonCache.getSeasonListByTvShowId(tvShowId)
        } else {
            seasonCache.getSeasonListByTvShowId(tvShowId)
        }
    }

    override suspend fun updateTvShowsDetails(tvShowId: Int) {

        val seasonsEntityList = apiService.getTvSeasonDetails(tvShowId)
            .toSeasonsEntityList()

        val tvShowCacheResult = tvShowCache.getTvShow(showId = tvShowId)

        tvShowCache.updateTvShowDetails(
            tvShowCacheResult.copy(
                seasonsList = seasonsEntityList
            )
        )

        //Insert Seasons
        seasonCache.insert(seasonsEntityList)
    }

}
