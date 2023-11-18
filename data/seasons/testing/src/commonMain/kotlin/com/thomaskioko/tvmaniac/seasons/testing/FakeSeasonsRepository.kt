package com.thomaskioko.tvmaniac.seasons.testing

import com.thomaskioko.tvmaniac.core.db.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSeasonsRepository : SeasonsRepository {

    private var seasonsList: Channel<List<Seasons>> = Channel(Channel.UNLIMITED)
    private var seasonsResult: Channel<Either<Failure, List<Seasons>>> = Channel(Channel.UNLIMITED)

    private var seasonEpisodesResult: Channel<Either<Failure, List<SeasonWithEpisodes>>> =
        Channel(Channel.UNLIMITED)

    suspend fun setSeasonWithEpisodes(result: Either<Failure, List<SeasonWithEpisodes>>) {
        seasonEpisodesResult.send(result)
    }

    suspend fun setSeasons(result: List<Seasons>) {
        seasonsList.send(result)
    }

    suspend fun setSeasonsResult(result: Either<Failure, List<Seasons>>) {
        seasonsResult.send(result)
    }

    override suspend fun getSeasons(traktId: Long): List<Seasons> = seasonsList.receive()

    override fun observeSeasonsStoreResponse(
        traktId: Long,
    ): Flow<Either<Failure, List<Seasons>>> = seasonsResult.receiveAsFlow()
}
