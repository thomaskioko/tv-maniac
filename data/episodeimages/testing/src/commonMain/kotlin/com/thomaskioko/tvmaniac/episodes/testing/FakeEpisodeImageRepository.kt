package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeEpisodeImageRepository : EpisodeImageRepository {

    override fun updateEpisodeImage(
        traktId: Long,
    ): Flow<Either<Failure, Unit>> = flowOf(Either.Right(Unit))
}
