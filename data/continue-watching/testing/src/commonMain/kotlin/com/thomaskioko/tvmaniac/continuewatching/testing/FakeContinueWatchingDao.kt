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

    override fun showIdsMissingShowDetails(): List<Long> = idsMissingShowDetails

    override fun upsert(entry: ContinueWatchingEntry) {
        state.value += (entry.showId to entry)
    }

    override fun upsertPlaceholder(showId: Long, tmdbId: Long?, title: String?, year: Long?) {
        if (state.value.containsKey(showId)) return
        state.value += (
            showId to ContinueWatchingEntry(
                showId = showId,
                tmdbId = tmdbId,
                airedEpisodes = 0L,
                completedCount = 0L,
                lastWatchedAt = 0L,
                lastUpdatedAt = 0L,
                title = title,
                year = year,
            )
            )
    }

    override fun deleteByTraktId(showId: Long) {
        state.value -= showId
    }

    override fun deleteAll() {
        state.value = emptyMap()
    }
}
