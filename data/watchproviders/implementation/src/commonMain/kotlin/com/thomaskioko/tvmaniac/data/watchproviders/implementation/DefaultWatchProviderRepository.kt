package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.core.store.mapToEither
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCH_PROVIDERS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultWatchProviderRepository(
  private val store: WatchProvidersStore,
  private val requestManagerRepository: RequestManagerRepository,
  private val dispatcher: AppCoroutineDispatchers,
) : WatchProviderRepository {

  override fun observeWatchProviders(
    id: Long,
    forceReload: Boolean
  ): Flow<Either<Failure, List<WatchProviders>>> {
    return store
      .stream(
        StoreReadRequest.cached(
          key = id,
          refresh =
            forceReload ||
              requestManagerRepository.isRequestExpired(
                entityId = id,
                requestType = WATCH_PROVIDERS.name,
                threshold = WATCH_PROVIDERS.duration,
              ),
        ),
      )
      .mapToEither()
      .flowOn(dispatcher.io)
  }
}
