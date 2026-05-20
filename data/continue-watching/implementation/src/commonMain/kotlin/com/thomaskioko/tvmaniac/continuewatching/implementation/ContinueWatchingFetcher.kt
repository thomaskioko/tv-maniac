package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry

public interface ContinueWatchingFetcher {

    /**
     * Fetches the current continue-watching list from Trakt.
     *
     * @param forceRefresh when true, bypasses any cache-hint optimizations. For
     * [ProgressContinueWatchingFetcher] this means passing `last_activity = null` to per-show
     * progress calls so Trakt returns full bodies. For [NitroContinueWatchingFetcher] this means
     * bypassing the empty-response guard so a user-initiated refresh writes through
     * even when the Nitro response is empty.
     * @return entries to persist (possibly empty when the user has no in-progress
     * shows), or `null` to signal "skip the write". Implementations return null on
     * any condition where overwriting the local table would lose information:
     * an HTTP error on a top-level call, or [NitroContinueWatchingFetcher]'s empty-response guard.
     */
    public suspend fun run(forceRefresh: Boolean): List<ContinueWatchingEntry>?
}
