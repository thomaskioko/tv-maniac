package com.thomaskioko.tvmaniac.watchstatus.testing

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchProgress
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

public class FakeShowWatchStatusDao : ShowWatchStatusDao {

    private val statuses = MutableStateFlow<Map<Long, WatchStatus>>(emptyMap())
    private val progress = mutableMapOf<Long, ShowWatchProgress>()

    public fun setWatchProgress(showId: Id<ShowId>, watchProgress: ShowWatchProgress) {
        progress[showId.id] = watchProgress
    }

    public fun setStatus(showId: Id<ShowId>, status: WatchStatus) {
        statuses.value = statuses.value + (showId.id to status)
    }

    override fun upsert(showId: Id<ShowId>, status: WatchStatus, lastWatchedAt: Long?, lastSyncedAt: Long?) {
        statuses.value = statuses.value + (showId.id to status)
    }

    override fun getStatus(showId: Id<ShowId>): WatchStatus? = statuses.value[showId.id]

    override fun observeStatus(showId: Id<ShowId>): Flow<WatchStatus?> = statuses.map { it[showId.id] }

    override fun getWatchProgress(showId: Id<ShowId>): ShowWatchProgress? = progress[showId.id]

    override fun delete(showId: Id<ShowId>) {
        statuses.value = statuses.value - showId.id
        progress.remove(showId.id)
    }

    override fun deleteAll() {
        statuses.value = emptyMap()
        progress.clear()
    }
}
