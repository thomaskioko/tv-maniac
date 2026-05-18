package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowsDao
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class FetchMissingShowsInteractor(
    private val watchedShowsDao: WatchedShowsDao,
    private val syncWatchedShowInteractor: SyncWatchedShowInteractor,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Boolean>() {

    override suspend fun doWork(params: Boolean) {
        withContext(dispatchers.io) {
            watchedShowsDao.traktIdsMissingShowDetails().forEach { traktId ->
                syncWatchedShowInteractor.executeSync(
                    SyncWatchedShowInteractor.Param(traktId = traktId, forceRefresh = params),
                )
            }
        }
    }
}
