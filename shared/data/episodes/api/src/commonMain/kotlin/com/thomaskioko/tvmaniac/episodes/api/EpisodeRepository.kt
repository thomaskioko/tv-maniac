package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow


interface EpisodeRepository {

    fun updateEpisodeArtWork(): Flow<Either<Failure, Unit>>
}
