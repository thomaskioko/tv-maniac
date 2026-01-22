package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCH_PROVIDERS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
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
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
) : WatchProviderRepository {

    override suspend fun fetchWatchProviders(traktId: Long, forceRefresh: Boolean) {
        val tmdbId = tvShowsDao.getTmdbIdByTraktId(traktId) ?: return
        val isExpired = requestManagerRepository.isRequestExpired(
            entityId = tmdbId,
            requestType = WATCH_PROVIDERS.name,
            threshold = WATCH_PROVIDERS.duration,
        )

        when {
            forceRefresh || isExpired -> store.fresh(traktId)
            else -> store.get(traktId)
        }
    }

    override fun observeWatchProviders(traktId: Long): Flow<List<WatchProviders>> {
        val tmdbId = tvShowsDao.getTmdbIdByTraktId(traktId) ?: return emptyFlow()
        return dao.observeWatchProviders(tmdbId)
    }
}
