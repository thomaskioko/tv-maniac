package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.FEATURED_SHOWS_TODAY
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class DefaultFeaturedShowsRepository(
  private val store: FeaturedShowsStore,
  private val requestManagerRepository: RequestManagerRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : FeaturedShowsRepository {

  override suspend fun updateFeaturedShows(
    timeWindow: String,
    forceRefresh: Boolean,
  ): Flow<Either<Failure, List<ShowEntity>>> {
    val refresh =
      forceRefresh ||
        requestManagerRepository.isRequestExpired(
          entityId = FEATURED_SHOWS_TODAY.requestId,
          requestType = FEATURED_SHOWS_TODAY.name,
          threshold = FEATURED_SHOWS_TODAY.duration,
        )
    return store
      .stream(
        StoreReadRequest.cached(
          key = timeWindow,
          refresh = refresh,
        ),
      )
      .mapResult(getShows(timeWindow))
      .flowOn(dispatchers.io)
  }

  private suspend fun getShows(timeWindow: String): List<ShowEntity> = store.get(key = timeWindow)
}
