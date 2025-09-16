package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Tvshow

/**
 * Create a minimal placeholder show for category list entries.
 * This ensures we have a valid reference without triggering unnecessary updates.
 */
fun createShowPlaceholder(
    id: Long,
    name: String = "",
    overview: String = "",
    posterPath: String? = null,
    popularity: Double = 0.0,
    voteAverage: Double = 0.0,
    voteCount: Long = 0L,
    genreIds: List<Int> = emptyList(),
    firstAirDate: String? = null,
): Tvshow = Tvshow(
    id = Id(id),
    name = name,
    overview = overview,
    language = null,
    status = null,
    first_air_date = firstAirDate,
    popularity = popularity,
    episode_numbers = null,
    last_air_date = null,
    season_numbers = null,
    vote_average = voteAverage,
    vote_count = voteCount,
    genre_ids = genreIds,
    poster_path = posterPath,
    backdrop_path = null,
)
