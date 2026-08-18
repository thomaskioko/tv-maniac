package com.thomaskioko.tvmaniac.data.backup.api

public interface BackupRepository {
    public suspend fun createBackup(): BackupFile

    public suspend fun writeBackup(destination: BackupDestination): BackupResult

    public suspend fun restoreBackup(source: BackupDestination): RestoreResult
}

public interface BackupDestination {
    public fun write(contents: String)

    public fun read(): String
}

public interface BackupDestinationBuilder {
    public fun build(location: String): BackupDestination

    public fun safetyCopy(): BackupDestination
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

public sealed interface RestoreResult {
    public data class Restored(val summary: RestoreSummary) : RestoreResult

    public data class Failed(val reason: RestoreFailure, val cause: Throwable? = null) : RestoreResult
}

public data class RestoreSummary(
    val showCount: Int,
    val episodeCount: Int,
    val skippedShows: List<String> = emptyList(),
    val skippedSeasonRatings: Int = 0,
    val skippedEpisodeRatings: Int = 0,
    val rewatchSessionsKept: Int = 0,
)

public enum class RestoreFailure {
    SyncInProgress,
    ReadFailed,
    VersionTooNew,
    SafetyCopyFailed,
    ImportFailed,
}

public object BackupFormat {
    public const val VERSION: Int = 1
    public const val FILE_PREFIX: String = "tvmaniac-backup-"
    public const val FILE_EXTENSION: String = ".json"
    public const val MIME_TYPE: String = "application/json"
    public const val SAFETY_COPY_NAME: String = "tvmaniac-pre-restore.json"
}
