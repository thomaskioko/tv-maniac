package com.thomaskioko.tvmaniac.data.backup.api

public sealed interface RestoreResult {
    public data class Restored(val summary: RestoreSummary) : RestoreResult

    public data class Failed(val reason: RestoreFailure, val cause: Throwable? = null) : RestoreResult
}
