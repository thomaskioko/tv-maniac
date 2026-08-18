package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupRepository {
    public suspend fun createBackup(): BackupFile

    public suspend fun writeBackup(destination: BackupDestination): BackupResult

    public suspend fun restoreBackup(source: BackupDestination): RestoreResult
}
