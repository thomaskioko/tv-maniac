package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.core.db.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSeasonDetailsRepository : SeasonDetailsRepository {

    private val seasonsResult: Channel<Either<Failure, List<SeasonWithEpisodes>>> = Channel(Channel.UNLIMITED)
    private val cachedResult: Channel<Either<Failure, List<SeasonWithEpisodes>>> = Channel(Channel.UNLIMITED)

    suspend fun setSeasonsResult(result: Either<Failure, List<SeasonWithEpisodes>>) {
        seasonsResult.send(result)
    }

    suspend fun setCachedResults(result: Either<Failure, List<SeasonWithEpisodes>>) {
        cachedResult.send(result)
    }

    override fun observeCachedSeasonDetails(
        traktId: Long,
    ): Flow<Either<Failure, List<SeasonWithEpisodes>>> = cachedResult.receiveAsFlow()

    override fun observeSeasonDetailsStream(
        traktId: Long,
    ): Flow<Either<Failure, List<SeasonWithEpisodes>>> = seasonsResult.receiveAsFlow()
}
