package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class DefaultSimilarShowsRepository(
  private val store: SimilarShowStore,
  private val requestManagerRepository: RequestManagerRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : SimilarShowsRepository {

  override fun observeSimilarShows(
    id: Long,
    forceReload: Boolean
  ): Flow<Either<Failure, List<SimilarShows>>> {
    return store
      .stream(
        StoreReadRequest.cached(
          key = SimilarParams(showId = id, page = DEFAULT_API_PAGE),
          refresh =
            forceReload ||
              requestManagerRepository.isRequestExpired(
                entityId = id,
                requestType = RequestTypeConfig.SIMILAR_SHOWS.name,
                threshold = RequestTypeConfig.SIMILAR_SHOWS.duration,
              ),
        ),
      )
      .mapResult()
      .flowOn(dispatchers.io)
  }
}
