package com.thomaskioko.tvmaniac.data.watchproviders.api

import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface WatchProviderRepository {
  fun observeWatchProviders(
    id: Long,
    forceReload: Boolean = false
  ): Flow<Either<Failure, List<WatchProviders>>>
}
