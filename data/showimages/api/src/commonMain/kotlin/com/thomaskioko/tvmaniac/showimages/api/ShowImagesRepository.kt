package com.thomaskioko.tvmaniac.showimages.api

import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface ShowImagesRepository {
    fun updateShowArtWork(): Flow<Either<Failure, Unit>>
}
