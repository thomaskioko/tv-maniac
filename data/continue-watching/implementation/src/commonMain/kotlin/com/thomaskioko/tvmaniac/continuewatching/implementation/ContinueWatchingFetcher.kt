package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry

public interface ContinueWatchingFetcher {

    public suspend fun run(forceRefresh: Boolean): List<ContinueWatchingEntry>?
}
