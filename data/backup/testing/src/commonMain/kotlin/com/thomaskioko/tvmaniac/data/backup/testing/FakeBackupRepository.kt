package com.thomaskioko.tvmaniac.data.backup.testing

import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestinationBuilder
import com.thomaskioko.tvmaniac.data.backup.api.BackupFile
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.BackupResult

public class FakeBackupRepository : BackupRepository {

    private var backup: BackupFile = BackupFile(
        version = BackupFormat.VERSION,
        createdAt = "2026-01-01T00:00:00Z",
        appVersion = "1.0.0",
    )
    private var writeResult: BackupResult = BackupResult.Written(showCount = 0, episodeCount = 0)
    private var createException: Throwable? = null

    public var lastDestination: BackupDestination? = null
        private set

    public fun setBackup(file: BackupFile) {
        backup = file
    }

    public fun setWriteResult(result: BackupResult) {
        writeResult = result
    }

    public fun setCreateException(exception: Throwable) {
        createException = exception
    }

    override suspend fun createBackup(): BackupFile {
        createException?.let { throw it }
        return backup
    }

    override suspend fun writeBackup(destination: BackupDestination): BackupResult {
        lastDestination = destination
        createException?.let { throw it }
        return writeResult
    }
}

public class FakeBackupDestination(private var contents: String = "") : BackupDestination {

    private var writeException: Throwable? = null
    private var readException: Throwable? = null

    public var written: String? = null
        private set

    public fun setWriteException(exception: Throwable) {
        writeException = exception
    }

    public fun setReadException(exception: Throwable) {
        readException = exception
    }

    public fun setContents(value: String) {
        contents = value
    }

    override fun write(contents: String) {
        writeException?.let { throw it }
        written = contents
        this.contents = contents
    }

    override fun read(): String {
        readException?.let { throw it }
        return contents
    }
}

public class FakeBackupDestinationBuilder(
    private val destination: BackupDestination = FakeBackupDestination(),
) : BackupDestinationBuilder {

    public var lastLocation: String? = null
        private set

    override fun build(location: String): BackupDestination {
        lastLocation = location
        return destination
    }
}
