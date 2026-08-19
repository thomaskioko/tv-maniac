package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.ResultInteractor
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.RestoreResult
import dev.zacsweers.metro.Inject

@Inject
public class RestoreBackupInteractor(
    private val backupRepository: BackupRepository,
) : ResultInteractor<RestoreBackupInteractor.Params, RestoreResult>() {

    public data class Params(val location: String)

    override suspend fun doWork(params: Params): RestoreResult = backupRepository.restoreBackup(params.location)
}
