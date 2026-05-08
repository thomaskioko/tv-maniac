package com.thomaskioko.tvmaniac.util.api

public sealed interface SyncError {
    public val showTraktId: Long
    public val cause: Throwable

    public data class MarkWatchedFailed(
        override val showTraktId: Long,
        override val cause: Throwable,
    ) : SyncError

    public data class MarkUnwatchedFailed(
        override val showTraktId: Long,
        override val cause: Throwable,
    ) : SyncError

    public data class BatchMarkFailed(
        override val showTraktId: Long,
        override val cause: Throwable,
    ) : SyncError
}
