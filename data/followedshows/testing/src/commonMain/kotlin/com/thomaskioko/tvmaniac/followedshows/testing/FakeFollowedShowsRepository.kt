package com.thomaskioko.tvmaniac.followedshows.testing

import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import kotlinx.coroutines.flow.Flow
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

    override suspend fun syncFollowedShows() { }

    override fun observeFollowedShows(): Flow<List<FollowedShowEntry>> = entries

    override suspend fun addFollowedShow(tmdbId: Long) {
        _addedShowIds.add(tmdbId)
        val newEntry = FollowedShowEntry(
            id = tmdbId,
            tmdbId = tmdbId,
            followedAt = Clock.System.now(),
        )
        entries.value += newEntry
    }

    override suspend fun removeFollowedShow(tmdbId: Long) {
        _removedShowIds.add(tmdbId)
        entries.value = entries.value.filter { it.tmdbId != tmdbId }
    }

    override suspend fun needsSync(expiry: Duration): Boolean = needsSyncResult

    public fun setEntries(newEntries: List<FollowedShowEntry>) {
        entries.value = newEntries
    }

    public fun reset() {
        _addedShowIds.clear()
        _removedShowIds.clear()
        entries.value = emptyList()
    }
}
