package com.thomaskioko.tvmaniac.watchstatus.api

import com.thomaskioko.tvmaniac.db.WatchStatus
import kotlinx.coroutines.flow.Flow

public interface ShowWatchStatusRepository {
    public suspend fun refresh(showId: Long)

    public fun observeShowCountsByStatus(): Flow<Map<WatchStatus, Long>>
}
