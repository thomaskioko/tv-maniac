package com.thomaskioko.tvmaniac.data.watchproviders.api

import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface WatchProviderRepository {
  suspend fun fetchWatchProviders(id: Long): List<WatchProviders>

  fun observeWatchProviders(id: Long): Flow<Either<Failure, List<WatchProviders>>>
}
