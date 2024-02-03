package com.thomaskioko.tvmaniac.data.watchproviders.testing

import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

class FakeWatchProviderRepository : WatchProviderRepository {
    override suspend fun fetchWatchProviders(id: Long): List<WatchProviders> {
        TODO("Not yet implemented")
    }

    override fun observeWatchProviders(id: Long): Flow<Either<Failure, List<WatchProviders>>> {
        TODO("Not yet implemented")
    }
}
