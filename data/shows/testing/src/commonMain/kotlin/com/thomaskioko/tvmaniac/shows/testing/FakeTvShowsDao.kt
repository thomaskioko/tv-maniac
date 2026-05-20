package com.thomaskioko.tvmaniac.shows.testing

import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.mergeShows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

public class FakeTvShowsDao : TvShowsDao {

    private val state = MutableStateFlow<Map<Long, Tvshow>>(emptyMap())

    public fun entries(): List<Tvshow> = state.value.values.toList()

    override fun upsert(show: Tvshow) {
        state.value += (show.trakt_id.id to show)
    }

    override fun upsert(list: List<Tvshow>) {
        state.value += list.associateBy { it.trakt_id.id }
    }

    override fun upsertMerging(show: Tvshow) {
        val existing = state.value[show.trakt_id.id]
        val merged = mergeShows(local = existing, network = show)
        state.value = state.value + (merged.trakt_id.id to merged)
    }

    override fun observeShowsByQuery(query: String): Flow<List<ShowEntity>> =
        state.asStateFlow().map { emptyList() }

    override fun observeQueryCount(query: String): Flow<Long> =
        state.asStateFlow().map { 0L }

    override suspend fun getQueryCount(query: String): Long = 0L

    override fun deleteTvShows() {
        state.value = emptyMap()
    }

    override fun getShowsByTraktIds(traktIds: List<Long>): List<ShowEntity> = emptyList()

    override fun getTmdbIdByTraktId(traktId: Long): Long? = state.value[traktId]?.tmdb_id?.id

    override suspend fun existsByTraktId(traktId: Long): Boolean = traktId in state.value
}
