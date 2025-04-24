package com.thomaskioko.tvmaniac.data.watchproviders.api

import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface WatchProviderRepository {
  suspend fun fetchWatchProviders(
    id: Long,
    forceRefresh: Boolean = false
  )

  fun observeWatchProviders(id: Long): Flow<List<WatchProviders>>
}
