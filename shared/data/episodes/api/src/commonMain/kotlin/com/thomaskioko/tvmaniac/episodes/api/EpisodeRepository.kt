package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow


interface EpisodeRepository {

    fun updateEpisodeArtWork(): Flow<Either<Failure, Unit>>
}
