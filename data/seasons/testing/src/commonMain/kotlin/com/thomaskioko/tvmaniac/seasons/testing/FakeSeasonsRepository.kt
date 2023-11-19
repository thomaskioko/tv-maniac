package com.thomaskioko.tvmaniac.seasons.testing

import com.thomaskioko.tvmaniac.core.db.SeasonsByShowId
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSeasonsRepository : SeasonsRepository {

    private var seasonsList: Channel<List<SeasonsByShowId>> = Channel(Channel.UNLIMITED)
    private var seasonsResult: Channel<Either<Failure, List<SeasonsByShowId>>> = Channel(Channel.UNLIMITED)

    private var seasonEpisodesResult: Channel<Either<Failure, List<SeasonsByShowId>>> =
        Channel(Channel.UNLIMITED)

    suspend fun setSeasonWithEpisodes(result: Either<Failure, List<SeasonsByShowId>>) {
        seasonEpisodesResult.send(result)
    }

    suspend fun setSeasons(result: List<SeasonsByShowId>) {
        seasonsList.send(result)
    }

    suspend fun setSeasonsResult(result: Either<Failure, List<SeasonsByShowId>>) {
        seasonsResult.send(result)
    }

    override suspend fun fetchSeasonsByShowId(traktId: Long): List<SeasonsByShowId> = seasonsList.receive()

    override fun observeSeasonsByShowId(
        traktId: Long,
    ): Flow<Either<Failure, List<SeasonsByShowId>>> = seasonsResult.receiveAsFlow()
}
