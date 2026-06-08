package com.thomaskioko.tvmaniac.syncstate.api

public sealed interface SyncError {
    public val cause: Throwable

    public data class MarkWatchedFailed(
        public val showId: Long,
        override val cause: Throwable,
    ) : SyncError

    public data class MarkUnwatchedFailed(
        public val showId: Long,
        override val cause: Throwable,
    ) : SyncError

    public data class BatchMarkFailed(
        public val showId: Long,
        override val cause: Throwable,
    ) : SyncError

    public data class BackgroundSyncFailed(
        public val operationId: String,
        override val cause: Throwable,
    ) : SyncError

    public data class AccountLimitExceeded(
        public val message: String,
        override val cause: Throwable,
    ) : SyncError
}
