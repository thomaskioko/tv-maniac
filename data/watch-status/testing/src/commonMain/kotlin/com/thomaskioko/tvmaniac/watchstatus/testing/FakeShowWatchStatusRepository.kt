package com.thomaskioko.tvmaniac.watchstatus.testing

import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusRepository

public class FakeShowWatchStatusRepository : ShowWatchStatusRepository {

    private val refreshed = mutableListOf<Long>()

    public fun refreshedShowIds(): List<Long> = refreshed.toList()

    override suspend fun refresh(showId: Long) {
        refreshed.add(showId)
    }
}
