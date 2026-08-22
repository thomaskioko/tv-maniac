package com.thomaskioko.tvmaniac.data.backup.api.model

public enum class RestoreFailure {
    SyncInProgress,
    ReadFailed,
    VersionTooNew,
    SafetyCopyFailed,
    ImportFailed,
}
