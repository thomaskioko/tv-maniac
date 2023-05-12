package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeSeasonDetailsRepository : SeasonDetailsRepository {

    private var seasonsResult = flowOf<Either<Failure, List<Seasons>>>()

    private var seasonEpisodesResult = flowOf<Either<Failure, List<SelectSeasonWithEpisodes>>>()

    suspend fun setSeasonsResult(result: Either<Failure, List<Seasons>>) {
        seasonsResult = flow { emit(result) }
    }

    suspend fun setSeasonDetails(result: Either<Failure, List<SelectSeasonWithEpisodes>>) {
        seasonEpisodesResult = flow { emit(result) }
    }

    override fun observeSeasonsStream(traktId: Long): Flow<Either<Failure, List<Seasons>>> = seasonsResult

    override fun observeSeasonDetails(traktId: Long): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>> =
        seasonEpisodesResult

    override fun observeSeasonDetailsStream(
        traktId: Long,
    ): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>> = seasonEpisodesResult
}
