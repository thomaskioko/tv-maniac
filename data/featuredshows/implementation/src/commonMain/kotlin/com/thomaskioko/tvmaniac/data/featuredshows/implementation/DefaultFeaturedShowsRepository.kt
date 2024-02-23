package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.filterForResult
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.FEATURED_SHOWS_TODAY
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadRequest.Companion.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class DefaultFeaturedShowsRepository(
  private val store: FeaturedShowsStore,
  private val requestManagerRepository: RequestManagerRepository,
  private val dispatchers: AppCoroutineDispatchers,
) : FeaturedShowsRepository {

  override suspend fun fetchFeaturedShows(
    timeWindow: String,
    forceRefresh: Boolean,
  ): List<ShowEntity> {
    return if (forceRefresh) {
      store.stream(fresh(key = timeWindow)).filterForResult().first().dataOrNull()
        ?: getShows(timeWindow)
    } else {
      getShows(timeWindow)
    }
  }

  private suspend fun getShows(timeWindow: String): List<ShowEntity> = store.get(key = timeWindow)

  override fun observeFeaturedShows(timeWindow: String): Flow<Either<Failure, List<ShowEntity>>> =
    store
      .stream(
        StoreReadRequest.cached(
          key = timeWindow,
          refresh =
            requestManagerRepository.isRequestExpired(
              entityId = FEATURED_SHOWS_TODAY.requestId,
              requestType = FEATURED_SHOWS_TODAY.name,
              threshold = FEATURED_SHOWS_TODAY.duration,
            ),
        ),
      )
      .mapResult()
      .flowOn(dispatchers.io)
}
