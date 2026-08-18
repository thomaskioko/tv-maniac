package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.RestoreFailedException
import com.thomaskioko.tvmaniac.data.backup.api.RestoreResult
import com.thomaskioko.tvmaniac.data.backup.api.RestoreSummary
import dev.zacsweers.metro.Inject

@Inject
public class RestoreBackupInteractor(
    private val backupRepository: BackupRepository,
) : Interactor<RestoreBackupInteractor.Params>() {

    public var lastSummary: RestoreSummary? = null
        private set

    public data class Params(val location: String)

    override suspend fun doWork(params: Params) {
        lastSummary = null
        when (val result = backupRepository.restoreBackup(params.location)) {
            is RestoreResult.Restored -> lastSummary = result.summary
            is RestoreResult.Failed -> throw RestoreFailedException(result.reason, result.cause)
        }
    }
}
