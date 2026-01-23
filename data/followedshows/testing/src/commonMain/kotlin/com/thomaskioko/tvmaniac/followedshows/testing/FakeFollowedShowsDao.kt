package com.thomaskioko.tvmaniac.followedshows.testing

import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

public class FakeFollowedShowsDao : FollowedShowsDao {

    private val entriesFlow = MutableStateFlow<List<FollowedShowEntry>>(emptyList())
    private var nextId = 1L

    override fun entries(): List<FollowedShowEntry> = entriesFlow.value

    override fun entriesObservable(): Flow<List<FollowedShowEntry>> = entriesFlow

    override fun entryWithTraktId(traktId: Long): FollowedShowEntry? =
        entriesFlow.value.find { it.traktId == traktId }

    override fun entriesWithNoPendingAction(): List<FollowedShowEntry> =
        entriesFlow.value.filter { it.pendingAction == PendingAction.NOTHING }

    override fun entriesWithUploadPendingAction(): List<FollowedShowEntry> =
        entriesFlow.value.filter { it.pendingAction == PendingAction.UPLOAD }

    override fun entriesWithDeletePendingAction(): List<FollowedShowEntry> =
        entriesFlow.value.filter { it.pendingAction == PendingAction.DELETE }

    override fun upsert(entry: FollowedShowEntry): Long {
        val existing = entriesFlow.value.find { it.traktId == entry.traktId }
        val id = if (entry.id > 0) entry.id else existing?.id ?: nextId++
        val newEntry = entry.copy(id = id)

        entriesFlow.value = if (existing != null) {
            entriesFlow.value.map { if (it.traktId == entry.traktId) newEntry else it }
        } else {
            entriesFlow.value + newEntry
        }
        return id
    }

    override fun updatePendingAction(id: Long, action: PendingAction) {
        entriesFlow.value = entriesFlow.value.map {
            if (it.id == id) it.copy(pendingAction = action) else it
        }
    }

    override fun deleteById(id: Long) {
        entriesFlow.value = entriesFlow.value.filter { it.id != id }
    }

    override fun deleteByTraktId(traktId: Long) {
        entriesFlow.value = entriesFlow.value.filter { it.traktId != traktId }
    }

    public fun clear() {
        entriesFlow.value = emptyList()
        nextId = 1L
    }
}
