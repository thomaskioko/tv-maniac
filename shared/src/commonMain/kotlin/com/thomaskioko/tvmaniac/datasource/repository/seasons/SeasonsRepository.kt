package com.thomaskioko.tvmaniac.datasource.repository.seasons

import com.thomaskioko.tvmaniac.presentation.model.Season

interface SeasonsRepository  {

    suspend fun getSeasonListByTvShowId(tvShowId: Int) : List<Season>

    suspend fun updateTvShowsDetails(tvShowId: Int)
}