package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.util.AppUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class DefaultTrailerRepository(
  private val appUtils: AppUtils,
  private val trailerDao: TrailerDao,
  private val dispatchers: AppCoroutineDispatchers,
) : TrailerRepository {

  override fun isYoutubePlayerInstalled(): Flow<Boolean> = appUtils.isYoutubePlayerInstalled()

  override fun observeTrailers(id: Long): Flow<Either<Failure, List<Trailers>>> =
    trailerDao
      .observeTrailersById(id)
      .distinctUntilChanged()
      .map { Either.Right(it) }
      .flowOn(dispatchers.io)
}
