package com.thomaskioko.tvmaniac.data.backup.api

public sealed interface BackupResult {
    public data class Written(val showCount: Int, val episodeCount: Int) : BackupResult

    public data class Failed(val reason: BackupFailure, val cause: Throwable? = null) : BackupResult
}
