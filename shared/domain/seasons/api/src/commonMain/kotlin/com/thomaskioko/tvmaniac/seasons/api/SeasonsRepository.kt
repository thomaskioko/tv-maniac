package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {

    fun observeShowSeasons(tvShowId: Long): Flow<Resource<List<SelectSeasonsByShowId>>>
}
