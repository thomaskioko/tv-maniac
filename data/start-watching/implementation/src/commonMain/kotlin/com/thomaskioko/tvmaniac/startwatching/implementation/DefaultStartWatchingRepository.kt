package com.thomaskioko.tvmaniac.startwatching.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingDao
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultStartWatchingRepository(
    private val dao: StartWatchingDao,
    private val startWatchingStore: StartWatchingWatchlistStore,
    private val accountManager: AccountManager,
    private val logger: Logger,
) : StartWatchingRepository {

    override fun observeStartWatching(): Flow<List<StartWatchingShow>> = dao.observeStartWatchingShows()

    override suspend fun syncWatchlist(forceRefresh: Boolean) {
        if (accountManager.getActiveProvider() == null) return

        when {
            forceRefresh -> startWatchingStore.fresh(Unit) { logger.debug(TAG, it) }
            else -> startWatchingStore.get(Unit) { logger.debug(TAG, it) }
        }
    }

    private companion object {
        private const val TAG = "StartWatchingRepository"
    }
}
