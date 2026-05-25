package com.thomaskioko.tvmaniac.startwatching.api

import kotlinx.coroutines.flow.Flow

public interface StartWatchingRepository {

    /**
     * Observes followed shows that are released but not yet started, excluding any show already
     * present in continue-watching.
     */
    public fun observeStartWatching(): Flow<List<StartWatchingShow>>
}
