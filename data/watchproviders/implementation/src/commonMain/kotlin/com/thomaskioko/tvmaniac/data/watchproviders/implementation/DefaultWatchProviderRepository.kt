package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCH_PROVIDERS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class DefaultWatchProviderRepository(
  private val store: WatchProvidersStore,
  private val requestManagerRepository: RequestManagerRepository,
  private val dispatcher: AppCoroutineDispatchers,
) : WatchProviderRepository {
  override suspend fun fetchWatchProviders(id: Long): List<WatchProviders> = store.get(id)

  override fun observeWatchProviders(id: Long): Flow<Either<Failure, List<WatchProviders>>> =
    store
      .stream(
        StoreReadRequest.cached(
          key = id,
          refresh =
            requestManagerRepository.isRequestExpired(
              entityId = id,
              requestType = WATCH_PROVIDERS.name,
              threshold = WATCH_PROVIDERS.duration,
            ),
        ),
      )
      .mapResult()
      .flowOn(dispatcher.io)
}
