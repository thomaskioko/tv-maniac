package com.thomaskioko.tvmaniac.followedshows.testing

import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Clock
import kotlin.time.Duration

public class FakeFollowedShowsRepository : FollowedShowsRepository {

    private val entries = MutableStateFlow<List<FollowedShowEntry>>(emptyList())
    private val _addedShowIds = mutableListOf<Long>()
    private val _removedShowIds = mutableListOf<Long>()

    public val addedShowIds: List<Long> get() = _addedShowIds
    public val removedShowIds: List<Long> get() = _removedShowIds
    public var needsSyncResult: Boolean = true

    override suspend fun syncFollowedShows(forceRefresh: Boolean) {
    }

    override suspend fun getFollowedShows(): List<FollowedShowEntry> = entries.value

    override suspend fun addFollowedShow(traktId: Long) {
        _addedShowIds.add(traktId)
        val newEntry = FollowedShowEntry(
            id = traktId,
            traktId = traktId,
            followedAt = Clock.System.now(),
        )
        entries.value += newEntry
    }

    override suspend fun removeFollowedShow(traktId: Long) {
        _removedShowIds.add(traktId)
        entries.value = entries.value.filter { it.traktId != traktId }
    }

    override suspend fun needsSync(expiry: Duration): Boolean = false

    public fun setEntries(newEntries: List<FollowedShowEntry>) {
        entries.value = newEntries
    }
}
