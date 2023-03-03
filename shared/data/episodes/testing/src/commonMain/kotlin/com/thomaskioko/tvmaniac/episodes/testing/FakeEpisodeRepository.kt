package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeEpisodeRepository : EpisodeRepository {

    override fun updateEpisodeArtWork(): Flow<Either<Failure, Unit>> = flowOf(Either.Right(Unit))
}