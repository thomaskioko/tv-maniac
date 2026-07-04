package com.thomaskioko.tvmaniac.shows.testing

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.mergeShows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

public class FakeTvShowsDao : TvShowsDao {

    private val state = MutableStateFlow<Map<Long, Tvshow>>(emptyMap())
    private val tmdbIdByLocalShowId = MutableStateFlow<Map<Long, Long>>(emptyMap())
    private val localShowIdByTmdbId = MutableStateFlow<Map<Long, Long>>(emptyMap())

    public fun entries(): List<Tvshow> = state.value.values.toList()

    public fun setTmdbIdForLocalShowId(showId: Long, tmdbId: Long) {
        tmdbIdByLocalShowId.value += (showId to tmdbId)
    }

    public fun setLocalShowIdForTmdbId(tmdbId: Long, showId: Long) {
        localShowIdByTmdbId.value += (tmdbId to showId)
    }

    override fun upsert(show: ShowToPersist) {
        val key = show.showId?.id ?: show.tmdbId.id
        state.value += (key to show.toTvshow())
    }

    override fun upsert(list: List<ShowToPersist>) {
        state.value += list.associate { show ->
            val key = show.showId?.id ?: show.tmdbId.id
            key to show.toTvshow()
        }
    }

    override fun upsertMerging(show: ShowToPersist) {
        val key = show.showId?.id ?: show.tmdbId.id
        val existing = state.value[key]
        val merged = mergeShows(local = existing, network = show)
        val mergedKey = merged.showId?.id ?: merged.tmdbId.id
        state.value = state.value + (mergedKey to merged.toTvshow())
    }

    override fun observeShowsByQuery(query: String): Flow<List<ShowEntity>> =
        state.asStateFlow().map { emptyList() }

    override fun observeQueryCount(query: String): Flow<Long> =
        state.asStateFlow().map { 0L }

    override suspend fun getQueryCount(query: String): Long = 0L

    override fun deleteTvShows() {
        state.value = emptyMap()
    }

    override fun getShowsByIds(showIds: List<Long>): List<ShowEntity> = emptyList()

    override fun getTmdbIdByShowId(showId: Long): Long? = state.value[showId]?.tmdb_id?.id

    override fun getTmdbIdForLocalShowId(showId: Long): Long? = tmdbIdByLocalShowId.value[showId]

    override fun getLocalShowIdByTmdbId(tmdbId: Long): Long? = localShowIdByTmdbId.value[tmdbId]

    override fun getTraktIdByTmdbId(tmdbId: Long): Long? = null

    override suspend fun existsByShowId(showId: Long): Boolean = showId in state.value
}

private fun ShowToPersist.toTvshow(): Tvshow = Tvshow(
    id = Id(0),
    tmdb_id = tmdbId,
    name = name,
    overview = overview,
    language = language,
    year = year,
    status = status,
    ratings = ratings,
    vote_count = voteCount,
    genres = genres,
    poster_path = posterPath,
    backdrop_path = backdropPath,
    episode_numbers = episodeNumbers,
    season_numbers = seasonNumbers,
)
