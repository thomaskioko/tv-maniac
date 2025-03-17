package com.thomaskioko.tvmaniac.data.watchproviders.testing

import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeWatchProviderRepository : WatchProviderRepository {
  private var watchProvidersResult: Channel<Either<Failure, List<WatchProviders>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setWatchProvidersResult(result: Either<Failure, List<WatchProviders>>) {
    watchProvidersResult.send(result)
  }

  override fun observeWatchProviders(
    id: Long,
    forceReload: Boolean
  ): Flow<Either<Failure, List<WatchProviders>>> = watchProvidersResult.receiveAsFlow()
}
