package com.thomaskioko.tvmaniac.domain.upnext

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.view.InvokeError
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Inject
public class UpNextTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    refreshUpNextInteractor: Lazy<RefreshUpNextInteractor>,
    datastoreRepo: Lazy<DatastoreRepository>,
    traktAuthRepo: Lazy<TraktAuthRepository>,
) {

    private val upNextInteractor by refreshUpNextInteractor
    private val datastoreRepository by datastoreRepo
    private val traktAuthRepository by traktAuthRepo

    public fun init() {
        observeDataSync()
        observeUpNextSync()
    }

    private fun observeDataSync() {
        coroutineScope.launch {
            traktAuthRepository.state
                .distinctUntilChanged()
                .drop(1)
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect {
                    upNextInteractor(true)
                        .onEach { status ->
                            if (status is InvokeError) {
                                logger.error(TAG, "Up next sync failed on login: ${status.throwable.message}")
                            }
                        }
                        .collect()
                }
        }
    }

    private fun observeUpNextSync() {
        coroutineScope.launch {
            combine(
                traktAuthRepository.state,
                datastoreRepository.observeBackgroundSyncEnabled(),
            ) { authState, syncEnabled ->
                authState == TraktAuthState.LOGGED_IN && syncEnabled
            }
                .distinctUntilChanged()
                .collect { shouldSync ->
                    when {
                        shouldSync -> {
                            scheduler.schedulePeriodic(UpNextSyncWorker.REQUEST)
                            logger.debug(TAG, "Up next sync scheduled")
                        }
                        else -> {
                            scheduler.cancel(UpNextSyncWorker.WORKER_NAME)
                            logger.debug(TAG, "Up next sync cancelled")
                        }
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "UpNextTasksInitializer"
    }
}

@ContributesTo(AppScope::class)
public interface UpNextTasksInitializerModule {
    public companion object {
        @Provides
        @IntoSet
        @AsyncInitializers
        public fun provideUpNextTasksInitializer(bind: UpNextTasksInitializer): () -> Unit = { bind.init() }
    }
}
