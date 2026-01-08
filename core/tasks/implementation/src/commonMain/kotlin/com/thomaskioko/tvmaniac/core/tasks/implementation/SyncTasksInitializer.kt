package com.thomaskioko.tvmaniac.core.tasks.implementation

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.SyncTasks
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class SyncTasksInitializer(
    private val syncTasks: SyncTasks,
    private val traktAuthRepository: TraktAuthRepository,
    private val datastoreRepository: DatastoreRepository,
    private val coroutineScope: AppCoroutineScope,
    private val logger: Logger,
) : AppInitializer {

    override fun init() {
        syncTasks.setup()

        coroutineScope.io.launch {
            combine(
                traktAuthRepository.state,
                datastoreRepository.observeBackgroundSyncEnabled(),
            ) { authState, syncEnabled ->
                authState to syncEnabled
            }
                .distinctUntilChanged()
                .collect { (authState, syncEnabled) ->
                    when {
                        authState == TraktAuthState.LOGGED_IN && syncEnabled -> {
                            logger.debug(TAG, "Scheduling library sync (logged in + enabled)")
                            syncTasks.scheduleLibrarySync()
                        }
                        else -> {
                            logger.debug(TAG, "Cancelling library sync (logged out or disabled)")
                            syncTasks.cancelLibrarySync()
                        }
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "SyncTasksInitializer"
    }
}
