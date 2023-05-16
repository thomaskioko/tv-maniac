package com.thomaskioko.tvmaniac.seasons.testing

import com.thomaskioko.tvmaniac.core.db.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.mobilenativefoundation.store.store5.StoreReadResponse

class FakeSeasonsRepository : SeasonsRepository {

    private var seasonsResult = flowOf<StoreReadResponse<List<Seasons>>>()

    private var seasonEpisodesResult = flowOf<Either<Failure, List<SeasonWithEpisodes>>>()

    suspend fun setSeasonsResult(result: StoreReadResponse<List<Seasons>>) {
        seasonsResult = flow { emit(result) }
    }

    suspend fun setSeasonDetails(result: Either<Failure, List<SeasonWithEpisodes>>) {
        seasonEpisodesResult = flow { emit(result) }
    }

    override fun observeSeasonsStoreResponse(traktId: Long): Flow<StoreReadResponse<List<Seasons>>> = seasonsResult
}
