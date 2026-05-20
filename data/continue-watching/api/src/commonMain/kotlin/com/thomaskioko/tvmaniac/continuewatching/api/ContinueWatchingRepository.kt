package com.thomaskioko.tvmaniac.continuewatching.api

public interface ContinueWatchingRepository {

    /**
     * Refreshes the local continue-watching table from Trakt.
     *
     * @param forceRefresh ignore the Store5 TTL validator and hit the network.
     * @param useNitro when true, fetches via `/sync/progress/up_next_nitro`. When false,
     *   the documented `/sync/watched/shows` + per-show `/progress/watched` path. The caller
     *   resolves the runtime feature-flag value (`CONTINUE_WATCHING_NITRO_ENABLED`) and
     *   passes it in; this repository does not observe the flag itself.
     */
    public suspend fun sync(forceRefresh: Boolean = false, useNitro: Boolean = false)
}
