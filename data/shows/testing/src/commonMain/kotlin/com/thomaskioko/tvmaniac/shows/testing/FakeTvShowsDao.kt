package com.thomaskioko.tvmaniac.shows.testing

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.mergeShows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.MutableStateFlow

public class FakeTvShowsDao : TvShowsDao {

    private val state = MutableStateFlow<Map<Long, Tvshow>>(emptyMap())
    private val tmdbIdByLocalShowId = MutableStateFlow<Map<Long, Long>>(emptyMap())
    private val localShowIdByTmdbId = MutableStateFlow<Map<Long, Long>>(emptyMap())
    private val traktIdByTmdbId = MutableStateFlow<Map<Long, Long>>(emptyMap())
    private val tmdbIdByTraktId = MutableStateFlow<Map<Long, Long>>(emptyMap())
    private val externalIds = MutableStateFlow<Map<Pair<Long, Provider>, String>>(emptyMap())

    public fun externalId(tmdbId: Long, provider: Provider): String? = externalIds.value[tmdbId to provider]

    public fun entries(): List<Tvshow> = state.value.values.toList()

    public fun setTmdbIdForLocalShowId(showId: Long, tmdbId: Long) {
        tmdbIdByLocalShowId.value += (showId to tmdbId)
    }

    public fun setLocalShowIdForTmdbId(tmdbId: Long, showId: Long) {
        localShowIdByTmdbId.value += (tmdbId to showId)
    }

    public fun setTraktIdForTmdbId(tmdbId: Long, traktId: Long) {
        traktIdByTmdbId.value += (tmdbId to traktId)
    }

    override fun upsert(show: ShowToPersist) {
        state.value += (show.tmdbId.id to show.toTvshow())
        recordTraktId(show)
    }

    override fun upsert(list: List<ShowToPersist>) {
        state.value += list.associate { show -> show.tmdbId.id to show.toTvshow() }
        list.forEach { recordTraktId(it) }
    }

    override fun upsertMerging(show: ShowToPersist) {
        val existing = state.value[show.tmdbId.id]
        val merged = mergeShows(local = existing, network = show)
        state.value = state.value + (merged.tmdbId.id to merged.toTvshow())
        recordTraktId(merged)
    }

    override fun upsertExternalId(tmdbId: Long, provider: Provider, externalId: String) {
        if (tmdbId !in state.value) return
        externalIds.value += ((tmdbId to provider) to externalId)
    }

    private fun recordTraktId(show: ShowToPersist) {
        val traktId = show.showId?.id ?: return
        traktIdByTmdbId.value += (show.tmdbId.id to traktId)
        tmdbIdByTraktId.value += (traktId to show.tmdbId.id)
    }

    override fun getTmdbIdsWithPoster(tmdbIds: List<Long>): Set<Long> =
        state.value.filterKeys { it in tmdbIds }
            .filterValues { !it.poster_path.isNullOrBlank() }
            .keys

    override fun deleteTvShows() {
        state.value = emptyMap()
    }

    override fun getShowsByIds(showIds: List<Long>): List<ShowEntity> = emptyList()

    override fun getTmdbIdByShowId(showId: Long): Long? = tmdbIdByTraktId.value[showId]

    override fun getTmdbIdForLocalShowId(showId: Long): Long? = tmdbIdByLocalShowId.value[showId]

    override fun getLocalShowIdByTmdbId(tmdbId: Long): Long? = localShowIdByTmdbId.value[tmdbId]

    override fun getTraktIdByTmdbId(tmdbId: Long): Long? = traktIdByTmdbId.value[tmdbId]

    override suspend fun existsByShowId(showId: Long): Boolean = showId in tmdbIdByTraktId.value
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
    runtime = null,
)
