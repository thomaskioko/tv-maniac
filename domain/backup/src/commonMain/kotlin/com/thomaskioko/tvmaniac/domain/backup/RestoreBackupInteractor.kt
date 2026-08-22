package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.ResultInteractor
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.model.RestoreResult
import dev.zacsweers.metro.Inject

@Inject
public class RestoreBackupInteractor(
    private val backupRepository: BackupRepository,
    private val taskScheduler: BackgroundTaskScheduler,
) : ResultInteractor<RestoreBackupInteractor.Params, RestoreResult>() {

    public data class Params(
        val location: String,
        val syncWithConnectedAccount: Boolean = false,
    )

    override suspend fun doWork(params: Params): RestoreResult {
        val result = backupRepository.restoreBackup(
            location = params.location,
            syncWithConnectedAccount = params.syncWithConnectedAccount,
        )
        if (result is RestoreResult.Restored) {
            taskScheduler.scheduleAndExecute(RestoredShowsRefillWorker.REQUEST)
        }
        return result
    }
}
