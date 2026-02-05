package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.db.WatchProvidersByTraktId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchProviderRepository(
    private val store: WatchProvidersStore,
    private val dao: WatchProviderDao,
) : WatchProviderRepository {

    override suspend fun fetchWatchProviders(traktId: Long, forceRefresh: Boolean) {
        when {
            forceRefresh -> store.fresh(traktId)
            else -> store.get(traktId)
        }
    }

    override fun observeWatchProviders(traktId: Long): Flow<List<WatchProviders>> =
        dao.observeWatchProvidersByTraktId(traktId)
            .map { providers -> providers.map { it.toWatchProviders() } }

    private fun WatchProvidersByTraktId.toWatchProviders(): WatchProviders =
        WatchProviders(
            provider_id = provider_id,
            name = name,
            logo_path = logo_path,
            tmdb_id = tmdb_id,
        )
}
