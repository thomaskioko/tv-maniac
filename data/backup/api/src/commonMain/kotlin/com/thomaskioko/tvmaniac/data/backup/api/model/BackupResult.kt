package com.thomaskioko.tvmaniac.data.backup.api.model

public sealed interface BackupResult {
    public data class Success(val showCount: Int, val episodeCount: Int) : BackupResult

    public data class Failed(val reason: BackupFailure, val cause: Throwable? = null) : BackupResult
}
