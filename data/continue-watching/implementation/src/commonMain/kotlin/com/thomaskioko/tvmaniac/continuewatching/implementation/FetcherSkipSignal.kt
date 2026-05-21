package com.thomaskioko.tvmaniac.continuewatching.implementation

/**
 * Signals that a fetcher chose to skip the Store5 writer for this cycle.
 *
 * Wrapped by the store's fetcher lambda in `FetcherResult.Error.Exception` so
 * Store5 propagates it out instead of calling the writer with empty data.
 * `DefaultContinueWatchingRepository` catches it silently — the local table
 * stays intact and no error reaches the presenter.
 */
internal class FetcherSkipSignal(message: String) : RuntimeException(message)
