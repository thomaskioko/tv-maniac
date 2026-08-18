package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.BackupResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
public class ExportBackupInteractor(
    private val backupRepository: BackupRepository,
) {
    public suspend operator fun invoke(location: String): BackupResult = backupRepository.writeBackup(location)
}
