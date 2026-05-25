package com.thomaskioko.tvmaniac.startwatching.testing

import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeStartWatchingRepository : StartWatchingRepository {

    private val startWatchingShows = MutableStateFlow<List<StartWatchingShow>>(emptyList())

    public fun setStartWatchingShows(shows: List<StartWatchingShow>) {
        startWatchingShows.value = shows
    }

    override fun observeStartWatching(): Flow<List<StartWatchingShow>> = startWatchingShows.asStateFlow()
}
