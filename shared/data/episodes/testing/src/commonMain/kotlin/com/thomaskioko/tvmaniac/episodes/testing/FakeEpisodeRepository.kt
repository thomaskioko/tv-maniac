package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeEpisodeRepository : EpisodeRepository {

    override fun updateEpisodeArtWork(): Flow<Either<Failure, Unit>> = flowOf(Either.Right(Unit))
}