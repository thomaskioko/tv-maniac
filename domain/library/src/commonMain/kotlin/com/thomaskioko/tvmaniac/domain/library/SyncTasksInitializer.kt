package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class SyncTasksInitializer(
    syncTasks: Lazy<SyncTasks>,
    syncLibraryInteractor: Lazy<SyncLibraryInteractor>,
    datastoreRepo: Lazy<DatastoreRepository>,
    traktAuthRepo: Lazy<TraktAuthRepository>,
    private val coroutineScope: AppCoroutineScope,
    private val logger: Logger,
) : AppInitializer {

    private val syncTask by syncTasks
    private val syncInteractor by syncLibraryInteractor
    private val datastoreRepository by datastoreRepo
    private val traktAuthRepository by traktAuthRepo

    override fun init() {
        syncTask.setup()
        observeDataSync()
        observeLibrarySync()
    }

    private fun observeDataSync() {
        coroutineScope.io.launch {
            traktAuthRepository.state
                .distinctUntilChanged()
                .drop(1)
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect {
                    withContext(NonCancellable) {
                        syncInteractor.executeSync(SyncLibraryInteractor.Param())
                        logger.debug(TAG, "Library sync completed successfully")
                    }
                }
        }
    }

    private fun observeLibrarySync() {
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
                        shouldSync -> syncTask.scheduleLibrarySync()
                        else -> syncTask.cancelLibrarySync()
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "SyncTasksInitializer"
    }
}
