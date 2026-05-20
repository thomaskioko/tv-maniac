package com.thomaskioko.tvmaniac.continuewatching.testing

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

public class FakeContinueWatchingDao : ContinueWatchingDao {

    private val state = MutableStateFlow<Map<Long, ContinueWatchingEntry>>(emptyMap())
    private var idsMissingShowDetails: List<Long> = emptyList()

    public fun setTraktIdsMissingShowDetails(ids: List<Long>) {
        idsMissingShowDetails = ids
    }

    override fun entries(): List<ContinueWatchingEntry> = state.value.values.toList()

    override fun entriesObservable(): Flow<List<ContinueWatchingEntry>> =
        state.map { it.values.toList() }

    override fun traktIdsMissingShowDetails(): List<Long> = idsMissingShowDetails

    override fun upsert(entry: ContinueWatchingEntry) {
        state.value = state.value + (entry.traktId to entry)
    }

    override fun deleteByTraktId(traktId: Long) {
        state.value = state.value - traktId
    }

    override fun deleteAll() {
        state.value = emptyMap()
    }
}
