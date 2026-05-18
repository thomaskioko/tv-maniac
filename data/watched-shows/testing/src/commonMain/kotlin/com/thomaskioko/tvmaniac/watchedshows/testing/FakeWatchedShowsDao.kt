package com.thomaskioko.tvmaniac.watchedshows.testing

import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowEntry
import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

public class FakeWatchedShowsDao : WatchedShowsDao {

    private val state = MutableStateFlow<Map<Long, WatchedShowEntry>>(emptyMap())

    override fun entries(): List<WatchedShowEntry> = state.value.values.toList()

    override fun entriesObservable(): Flow<List<WatchedShowEntry>> =
        state.map { it.values.toList() }

    override fun upsert(entry: WatchedShowEntry) {
        state.value = state.value + (entry.traktId to entry)
    }

    override fun deleteByTraktId(traktId: Long) {
        state.value = state.value - traktId
    }

    override fun deleteAll() {
        state.value = emptyMap()
    }
}
