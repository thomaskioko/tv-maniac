package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

@Inject
public class FetchMissingShowsInteractor(
    private val continueWatchingDao: ContinueWatchingDao,
    private val syncWatchedShowInteractor: SyncWatchedShowInteractor,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Boolean>() {

    override suspend fun doWork(params: Boolean) {
        withContext(dispatchers.io) {
            continueWatchingDao.traktIdsMissingShowDetails()
                .parallelForEach(concurrency = FETCH_CONCURRENCY) { traktId ->
                    ensureActive()
                    syncWatchedShowInteractor.executeSync(
                        SyncWatchedShowInteractor.Param(traktId = traktId, forceRefresh = params),
                    )
                }
        }
    }

    private companion object {
        private const val FETCH_CONCURRENCY = 3
    }
}
