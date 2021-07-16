package com.thomaskioko.tvmaniac.datasource.repository.seasons

import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity

interface SeasonsRepository  {

    suspend fun getSeasonListByTvShowId(tvShowId: Int) : List<SeasonsEntity>

    suspend fun updateTvShowsDetails(tvShowId: Int)
}