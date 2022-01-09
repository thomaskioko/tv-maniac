package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {

    fun observeShowSeasons(tvShowId: Int): Flow<Resource<List<SelectSeasonsByShowId>>>
}
