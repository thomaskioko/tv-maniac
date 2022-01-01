package com.thomaskioko.tvmaniac.datasource.repository.seasons

import com.thomaskioko.tvmaniac.presentation.model.SeasonUiModel
import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {

    fun getSeasonListByTvShowId(tvShowId: Int): Flow<Result<List<SeasonUiModel>>>

    suspend fun updateTvShowsDetails(tvShowId: Int)
}
