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

    public fun entries(): List<Tvshow> = state.value.values.toList()

    override fun upsert(show: ShowToPersist) {
        state.value += (show.showId.id to show.toTvshow())
    }

    override fun upsert(list: List<ShowToPersist>) {
        state.value += list.associate { it.showId.id to it.toTvshow() }
    }

    override fun upsertMerging(show: ShowToPersist) {
        val existing = state.value[show.showId.id]
        val merged = mergeShows(local = existing, network = show)
        state.value = state.value + (merged.showId.id to merged.toTvshow())
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
