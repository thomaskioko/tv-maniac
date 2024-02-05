package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.util.AppUtils
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class TrailerRepositoryImpl(
  private val appUtils: AppUtils,
  private val trailerDao: TrailerDao,
  private val dispatchers: AppCoroutineDispatchers,
) : TrailerRepository {

  override fun isYoutubePlayerInstalled(): Flow<Boolean> = appUtils.isYoutubePlayerInstalled()

  override suspend fun fetchTrailersByShowId(id: Long): List<Trailers> =
    trailerDao.getTrailersById(id)

  override fun observeTrailersStoreResponse(id: Long): Flow<Either<Failure, List<Trailers>>> =
    trailerDao
      .observeTrailersById(id)
      .distinctUntilChanged()
      .map { Either.Right(it) }
      .flowOn(dispatchers.io)
}
