package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeSeasonDetailsRepository : SeasonDetailsRepository {

    private var seasonsResult: Flow<Either<Failure, List<SelectSeasonsByShowId>>> =
        flowOf(Either.Right(data = null))

    private var seasonEpisodesResult: Flow<Either<Failure, List<SelectSeasonWithEpisodes>>> =
        flowOf()


    suspend fun setSeasonsResult(result: Either<Failure, List<SelectSeasonsByShowId>>) {
        seasonsResult = flow { emit(result) }
    }

    suspend fun setSeasonDetails(result: Either<Failure, List<SelectSeasonWithEpisodes>>) {
        seasonEpisodesResult = flow { emit(result) }
    }

    override fun observeShowSeasons(traktId: Int): Flow<Either<Failure, List<SelectSeasonsByShowId>>> =
        seasonsResult

    override fun observeSeasonEpisodes(showId: Int): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>> =
        flow {
            seasonEpisodesResult.collect {
                emit(it)
            }
        }

    override fun getSeasonEpisodes(showId: Int): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>> =
        seasonEpisodesResult
}