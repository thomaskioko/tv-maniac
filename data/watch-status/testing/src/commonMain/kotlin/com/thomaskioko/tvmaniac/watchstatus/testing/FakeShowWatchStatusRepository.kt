package com.thomaskioko.tvmaniac.watchstatus.testing

import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeShowWatchStatusRepository : ShowWatchStatusRepository {

    private val refreshed = mutableListOf<Long>()
    private val showCountsByStatus = MutableStateFlow<Map<WatchStatus, Long>>(emptyMap())

    public fun refreshedShowIds(): List<Long> = refreshed.toList()

    public fun setShowCountsByStatus(counts: Map<WatchStatus, Long>) {
        showCountsByStatus.value = counts
    }

    override fun observeShowCountsByStatus(): Flow<Map<WatchStatus, Long>> = showCountsByStatus.asStateFlow()

    override suspend fun refresh(showId: Long) {
        refreshed.add(showId)
    }
}
