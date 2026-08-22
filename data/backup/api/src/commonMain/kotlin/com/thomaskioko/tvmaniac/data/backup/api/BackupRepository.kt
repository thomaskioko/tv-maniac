package com.thomaskioko.tvmaniac.data.backup.api

import com.thomaskioko.tvmaniac.data.backup.api.model.BackupFile
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupResult
import com.thomaskioko.tvmaniac.data.backup.api.model.RestoreResult

public interface BackupRepository {
    public suspend fun createBackup(): BackupFile

    public suspend fun writeBackup(folder: String, fileName: String): BackupResult

    public suspend fun restoreBackup(
        location: String,
        syncWithConnectedAccount: Boolean = false,
    ): RestoreResult

    public suspend fun showsNeedingMetadata(): List<Long>
}
