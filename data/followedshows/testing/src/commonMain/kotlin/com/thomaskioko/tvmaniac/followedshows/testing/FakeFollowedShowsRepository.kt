package com.thomaskioko.tvmaniac.followedshows.testing

import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import kotlinx.coroutines.flow.MutableStateFlow
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

    public fun setEntries(newEntries: List<FollowedShowEntry>) {
        entries.value = newEntries
    }
}
