package com.thomaskioko.tvmaniac.showimages.api

import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface ShowImagesRepository {
    fun updateShowArtWork(): Flow<Either<Failure, Unit>>
}
