package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.backup.api.BackupExportException
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
public class ExportBackupInteractor(
    private val backupRepository: BackupRepository,
) : Interactor<ExportBackupInteractor.Params>() {

    public data class Params(val folder: String, val fileName: String)

    override suspend fun doWork(params: Params) {
        val result = backupRepository.writeBackup(params.folder, params.fileName)
        if (result is BackupResult.Failed) throw BackupExportException(result.reason, result.cause)
    }
}
