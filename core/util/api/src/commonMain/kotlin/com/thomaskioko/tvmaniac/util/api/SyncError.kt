package com.thomaskioko.tvmaniac.util.api

/**
 * A sync failure surfaced from a background push to Trakt. Published on [SyncErrorChannel] by
 * `DefaultEpisodeRepository` after a user-initiated mark-watched/unwatched action whose
 * follow-up `syncPendingEpisodes()` call fails. Subscribed by presenters that own the screen
 * the action originated from, which translate the error into a localised snackbar.
 */
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
