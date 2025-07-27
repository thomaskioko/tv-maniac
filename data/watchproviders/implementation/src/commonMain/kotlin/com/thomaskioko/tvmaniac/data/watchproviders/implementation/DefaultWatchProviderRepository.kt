package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCH_PROVIDERS
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultWatchProviderRepository(
    private val store: WatchProvidersStore,
    private val dao: WatchProviderDao,
    private val requestManagerRepository: RequestManagerRepository,
) : WatchProviderRepository {

    override suspend fun fetchWatchProviders(id: Long, forceRefresh: Boolean) {
        val isEmpty = dao.observeWatchProviders(id).first().isEmpty()
        val isExpired = requestManagerRepository.isRequestExpired(
            entityId = id,
            requestType = WATCH_PROVIDERS.name,
            threshold = WATCH_PROVIDERS.duration,
        )

        when {
            forceRefresh || isEmpty || isExpired -> store.fresh(id)
            else -> store.get(id)
        }
    }

    override fun observeWatchProviders(id: Long): Flow<List<WatchProviders>> {
        return dao.observeWatchProviders(id)
    }
}
