package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeEpisodeImageRepository : EpisodeImageRepository {

    override fun updateEpisodeImage(): Flow<Either<Failure, Unit>> = flowOf(Either.Right(Unit))
}
