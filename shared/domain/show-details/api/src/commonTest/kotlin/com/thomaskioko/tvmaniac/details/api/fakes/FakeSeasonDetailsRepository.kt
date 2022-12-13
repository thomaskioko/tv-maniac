package com.thomaskioko.tvmaniac.details.api.fakes

import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeSeasonDetailsRepository : SeasonDetailsRepository {

    private var seasonsResult: Flow<Resource<List<SelectSeasonsByShowId>>> =
        flowOf(Resource.Success(data = null))

    suspend fun setSeasonsResult(result: Resource<List<SelectSeasonsByShowId>>) {
        seasonsResult = flow { emit(result) }
    }

    override fun observeShowSeasons(traktId: Int): Flow<Resource<List<SelectSeasonsByShowId>>> =
        seasonsResult

    override fun updateSeasonEpisodes(showId: Int): Flow<Resource<List<SelectSeasonWithEpisodes>>> =
        flowOf(Resource.Success(emptyList()))

    override fun observeSeasonEpisodes(showId: Int): Flow<List<SelectSeasonWithEpisodes>> =
        flowOf(emptyList())
}