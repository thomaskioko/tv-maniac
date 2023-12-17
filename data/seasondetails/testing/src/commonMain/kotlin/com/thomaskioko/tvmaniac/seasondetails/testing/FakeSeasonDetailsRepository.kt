package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.core.db.Season_images
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSeasonDetailsRepository : SeasonDetailsRepository {

    private val seasonsResult: Channel<Either<Failure, SeasonDetailsWithEpisodes>> = Channel(Channel.UNLIMITED)
    private val cachedResult: Channel<SeasonDetailsWithEpisodes> = Channel(Channel.UNLIMITED)

    suspend fun setSeasonsResult(result: Either<Failure, SeasonDetailsWithEpisodes>) {
        seasonsResult.send(result)
    }

    suspend fun setCachedResults(result: SeasonDetailsWithEpisodes) {
        cachedResult.send(result)
    }

    override suspend fun fetchSeasonDetails(
        param: SeasonDetailsParam,
    ): SeasonDetailsWithEpisodes = cachedResult.receive()

    override fun observeSeasonDetailsStream(
        param: SeasonDetailsParam,
    ): Flow<Either<Failure, SeasonDetailsWithEpisodes>> = seasonsResult.receiveAsFlow()

    override fun fetchSeasonImages(id: Long): List<Season_images> = emptyList()

    override fun observeSeasonImages(id: Long): Flow<List<Season_images>> = emptyFlow()
}
