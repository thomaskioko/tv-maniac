package com.thomaskioko.tvmaniac.followedshows.testing

import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

public class FakeFollowedShowsRepository : FollowedShowsRepository {

    private val entries = MutableStateFlow<List<FollowedShowEntry>>(emptyList())
    private val _addedShowIds = mutableListOf<Long>()
    private val _removedShowIds = mutableListOf<Long>()

    public val addedShowIds: List<Long> get() = _addedShowIds
    public val removedShowIds: List<Long> get() = _removedShowIds
    public var needsSyncResult: Boolean = true

    override suspend fun getFollowedShows(): List<FollowedShowEntry> = entries.value

    override suspend fun addFollowedShow(showId: Long) {
        _addedShowIds.add(showId)
        val newEntry = FollowedShowEntry(
            id = showId,
            showId = showId,
            followedAt = Clock.System.now(),
        )
        entries.value += newEntry
    }

    override suspend fun removeFollowedShow(showId: Long) {
        _removedShowIds.add(showId)
        entries.value = entries.value.filter { it.showId != showId }
    }

    override fun observeIsFollowed(showId: Long): Flow<Boolean> =
        entries
            .map { list -> list.any { it.showId == showId && it.pendingAction != PendingAction.DELETE } }
            .distinctUntilChanged()

    public fun setEntries(newEntries: List<FollowedShowEntry>) {
        entries.value = newEntries
    }
}
