package com.thomaskioko.tvmaniac.tmdb.testing

import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeShowImagesRepository : ShowImagesRepository {

    override fun updateShowArtWork(): Flow<Either<Failure, Unit>> = flowOf(Either.Right(Unit))
}
