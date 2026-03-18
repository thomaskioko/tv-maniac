package com.thomaskioko.tvmaniac.domain.upnext

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.view.InvokeError
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class UpNextTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    refreshUpNextInteractor: Lazy<RefreshUpNextInteractor>,
    datastoreRepo: Lazy<DatastoreRepository>,
    traktAuthRepo: Lazy<TraktAuthRepository>,
    private val coroutineScope: AppCoroutineScope,
    private val logger: Logger,
) : AppInitializer {

    private val upNextInteractor by refreshUpNextInteractor
    private val datastoreRepository by datastoreRepo
    private val traktAuthRepository by traktAuthRepo

    override fun init() {
        observeDataSync()
        observeUpNextSync()
    }

    private fun observeDataSync() {
        coroutineScope.io.launch {
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
        coroutineScope.io.launch {
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
