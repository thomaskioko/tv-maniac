package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSeasonDetailsRepository : SeasonDetailsRepository {

    private val seasonsResult: Channel<Either<Failure, List<SeasonEpisodeDetailsById>>> = Channel(Channel.UNLIMITED)
    private val cachedResult: Channel<List<SeasonEpisodeDetailsById>> = Channel(Channel.UNLIMITED)

    suspend fun setSeasonsResult(result: Either<Failure, List<SeasonEpisodeDetailsById>>) {
        seasonsResult.send(result)
    }

    suspend fun setCachedResults(result: List<SeasonEpisodeDetailsById>) {
        cachedResult.send(result)
    }

    override suspend fun fetchSeasonDetails(
        traktId: Long,
    ): List<SeasonEpisodeDetailsById> = cachedResult.receive()

    override fun observeSeasonDetailsStream(
        traktId: Long,
    ): Flow<Either<Failure, List<SeasonEpisodeDetailsById>>> = seasonsResult.receiveAsFlow()
}
