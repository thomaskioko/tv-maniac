package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupRepository {
    public suspend fun createBackup(): BackupFile

    public suspend fun writeBackup(destination: BackupDestination): BackupResult
}

public interface BackupDestination {
    public fun write(contents: String)

    public fun read(): String
}

public interface BackupDestinationBuilder {
    public fun build(location: String): BackupDestination
}

public class BackupLocationUnreadableException(location: String) : Exception("Cannot open $location")

public sealed interface BackupResult {
    public data class Written(val showCount: Int, val episodeCount: Int) : BackupResult

    public data class Failed(val reason: BackupFailure, val cause: Throwable? = null) : BackupResult
}

public enum class BackupFailure {
    WriteFailed,
    VerificationFailed,
}

public object BackupFormat {
    public const val VERSION: Int = 1
    public const val FILE_PREFIX: String = "tvmaniac-backup-"
    public const val FILE_EXTENSION: String = ".json"
    public const val MIME_TYPE: String = "application/json"
}
