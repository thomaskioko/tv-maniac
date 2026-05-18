package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Inject
public class WatchlistTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    datastoreRepo: Lazy<DatastoreRepository>,
    traktAuthRepo: Lazy<TraktAuthRepository>,
) {

    private val datastoreRepository by datastoreRepo
    private val traktAuthRepository by traktAuthRepo

    public fun init() {
        coroutineScope.launch {
            combine(
                traktAuthRepository.state,
                datastoreRepository.observeBackgroundSyncEnabled(),
            ) { authState, syncEnabled ->
                authState == TraktAuthState.LOGGED_IN && syncEnabled
            }
                .distinctUntilChanged()
                .collect { shouldSync ->
                    if (shouldSync) {
                        scheduler.schedulePeriodic(WatchlistSyncWorker.REQUEST)
                    } else {
                        scheduler.cancel(WatchlistSyncWorker.WORKER_NAME)
                    }
                }
        }
    }
}
