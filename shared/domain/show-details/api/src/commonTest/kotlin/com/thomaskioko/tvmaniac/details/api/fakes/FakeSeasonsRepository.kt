package com.thomaskioko.tvmaniac.details.api.fakes

import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeSeasonsRepository : SeasonsRepository {

    private var seasonsResult: Flow<Resource<List<SelectSeasonsByShowId>>> =
        flowOf(Resource.Success(data = null))

    suspend fun setSeasonsResult(result: Resource<List<SelectSeasonsByShowId>>) {
        seasonsResult = flow { emit(result) }
    }

    override fun observeShowSeasons(traktId: Int): Flow<Resource<List<SelectSeasonsByShowId>>> =
        seasonsResult
}