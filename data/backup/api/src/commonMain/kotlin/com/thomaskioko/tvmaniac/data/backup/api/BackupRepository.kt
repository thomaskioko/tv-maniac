package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupRepository {
    public suspend fun createBackup(): BackupFile

    public suspend fun writeBackup(location: String): BackupResult

    public suspend fun restoreBackup(location: String): RestoreResult

    public suspend fun showsNeedingMetadata(): List<Long>
}
