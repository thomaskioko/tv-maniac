package com.thomaskioko.tvmaniac.episodeimages.api

import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface EpisodeImageRepository {

    fun updateEpisodeImage(): Flow<Either<Failure, Unit>>
}
