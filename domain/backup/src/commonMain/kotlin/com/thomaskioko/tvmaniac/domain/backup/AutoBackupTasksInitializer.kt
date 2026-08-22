package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Inject
public class AutoBackupTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
) {

    public fun init() {
        coroutineScope.launch {
            combine(
                datastoreRepository.value.observeAutoBackupEnabled(),
                datastoreRepository.value.observeAutoBackupLocation(),
                datastoreRepository.value.observeAutoBackupInterval(),
            ) { enabled, location, interval ->
                if (enabled && location != null) interval else null
            }
                .distinctUntilChanged()
                .collect { interval ->
                    if (interval != null) {
                        logger.debug(TAG, "Scheduling automatic backup every ${interval.days} days")
                        scheduler.schedulePeriodic(AutoBackupWorker.request(interval))
                    } else {
                        logger.debug(TAG, "Cancelling automatic backup")
                        scheduler.cancel(AutoBackupWorker.WORKER_NAME)
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "AutoBackupTasksInitializer"
    }
}
