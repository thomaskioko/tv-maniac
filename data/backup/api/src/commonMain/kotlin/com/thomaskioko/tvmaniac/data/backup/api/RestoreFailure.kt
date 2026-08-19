package com.thomaskioko.tvmaniac.data.backup.api

public enum class RestoreFailure {
    SyncInProgress,
    ReadFailed,
    VersionTooNew,
    SafetyCopyFailed,
    ImportFailed,
}
