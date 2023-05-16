package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.core.db.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.mobilenativefoundation.store.store5.StoreReadResponse

class FakeSeasonDetailsRepository : SeasonDetailsRepository {

    private var seasonsResult = flowOf<StoreReadResponse<List<Seasons>>>()

    private var seasonEpisodesResult = flowOf<Either<Failure, List<SeasonWithEpisodes>>>()

    suspend fun setSeasonsResult(result: StoreReadResponse<List<Seasons>>) {
        seasonsResult = flow { emit(result) }
    }

    suspend fun setSeasonDetails(result: Either<Failure, List<SeasonWithEpisodes>>) {
        seasonEpisodesResult = flow { emit(result) }
    }

    override fun observeSeasonDetails(traktId: Long): Flow<Either<Failure, List<SeasonWithEpisodes>>> =
        seasonEpisodesResult

    override fun observeSeasonDetailsStream(
        traktId: Long,
    ): Flow<Either<Failure, List<SeasonWithEpisodes>>> = seasonEpisodesResult
}
