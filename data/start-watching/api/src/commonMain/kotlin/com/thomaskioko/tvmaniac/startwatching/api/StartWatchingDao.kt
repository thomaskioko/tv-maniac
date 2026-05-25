package com.thomaskioko.tvmaniac.startwatching.api

import kotlinx.coroutines.flow.Flow

public interface StartWatchingDao {

    public fun observeStartWatchingShows(): Flow<List<StartWatchingShow>>
}
