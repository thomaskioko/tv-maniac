package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {

    fun observeShowSeasons(traktId: Int): Flow<Resource<List<SelectSeasonsByShowId>>>
}
