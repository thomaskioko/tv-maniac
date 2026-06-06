package com.thomaskioko.tvmaniac.watchstatus.api

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.WatchStatus
import kotlinx.coroutines.flow.Flow

public interface ShowWatchStatusDao {
    public fun upsert(showId: Id<ShowId>, status: WatchStatus, lastWatchedAt: Long?, lastSyncedAt: Long?)
    public fun getStatus(showId: Id<ShowId>): WatchStatus?
    public fun observeStatus(showId: Id<ShowId>): Flow<WatchStatus?>
    public fun getWatchProgress(showId: Id<ShowId>): ShowWatchProgress?
    public fun delete(showId: Id<ShowId>)
    public fun deleteAll()
}
