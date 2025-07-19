package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.db.WatchProviders
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultWatchProviderRepository(
    private val store: WatchProvidersStore,
    private val dao: WatchProviderDao,
) : WatchProviderRepository {

    override suspend fun fetchWatchProviders(id: Long, forceRefresh: Boolean) {
        when {
            forceRefresh -> store.fresh(id)
            else -> store.get(id)
        }
    }

    override fun observeWatchProviders(id: Long): Flow<List<WatchProviders>> {
        return dao.observeWatchProviders(id)
    }
}
