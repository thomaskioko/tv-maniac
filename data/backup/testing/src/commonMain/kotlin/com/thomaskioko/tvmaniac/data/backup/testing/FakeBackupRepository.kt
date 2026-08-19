package com.thomaskioko.tvmaniac.data.backup.testing

import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupFile
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.BackupResult
import com.thomaskioko.tvmaniac.data.backup.api.RestoreResult
import com.thomaskioko.tvmaniac.data.backup.api.RestoreSummary

public class FakeBackupRepository : BackupRepository {

    private var backup: BackupFile = BackupFile(
        version = BackupFormat.VERSION,
        createdAt = "2026-01-01T00:00:00Z",
        appVersion = "1.0.0",
    )
    private var writeResult: BackupResult = BackupResult.Success(showCount = 0, episodeCount = 0)
    private var restoreResult: RestoreResult = RestoreResult.Restored(RestoreSummary(showCount = 0, episodeCount = 0))
    private var createException: Throwable? = null

    public var lastWriteLocation: String? = null
        private set

    public var lastRestoreLocation: String? = null
    public var lastRestoreSyncedWithConnectedAccount: Boolean? = null
        private set

    private var showsNeedingMetadata: List<Long> = emptyList()

    public fun setShowsNeedingMetadata(ids: List<Long>) {
        showsNeedingMetadata = ids
    }

    public fun setBackup(file: BackupFile) {
        backup = file
    }

    public fun setWriteResult(result: BackupResult) {
        writeResult = result
    }

    public fun setRestoreResult(result: RestoreResult) {
        restoreResult = result
    }

    public fun setCreateException(exception: Throwable) {
        createException = exception
    }

    override suspend fun createBackup(): BackupFile {
        createException?.let { throw it }
        return backup
    }

    override suspend fun writeBackup(location: String): BackupResult {
        lastWriteLocation = location
        createException?.let { throw it }
        return writeResult
    }

    override suspend fun restoreBackup(location: String, syncWithConnectedAccount: Boolean): RestoreResult {
        lastRestoreLocation = location
        lastRestoreSyncedWithConnectedAccount = syncWithConnectedAccount
        return restoreResult
    }

    override suspend fun showsNeedingMetadata(): List<Long> = showsNeedingMetadata
}

public class FakeBackupDestination : BackupDestination {

    private val files = mutableMapOf<String, String>()
    private var writeException: Throwable? = null
    private var readException: Throwable? = null

    public var lastWriteLocation: String? = null
        private set

    public fun setWriteException(exception: Throwable) {
        writeException = exception
    }

    public fun setReadException(exception: Throwable) {
        readException = exception
    }

    public fun setContents(location: String, contents: String) {
        files[location] = contents
    }

    public fun contentsAt(location: String): String? = files[location]

    override fun write(location: String, contents: String) {
        writeException?.let { throw it }
        lastWriteLocation = location
        files[location] = contents
    }

    override fun read(location: String): String {
        readException?.let { throw it }
        return files[location] ?: throw IllegalStateException("Nothing written to $location")
    }

    override fun safetyCopyLocation(): String = SAFETY_COPY_LOCATION

    public companion object {
        public const val SAFETY_COPY_LOCATION: String = "safety-copy"
    }
}
