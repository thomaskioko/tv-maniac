package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Tvshow

public fun mergeShows(
    local: Tvshow?,
    network: Tvshow,
): Tvshow {
    if (local == null) return network

    return Tvshow(
        trakt_id = network.trakt_id,
        tmdb_id = network.tmdb_id,
        name = network.name,
        overview = network.overview.ifEmpty { local.overview },
        language = network.language ?: local.language,
        year = network.year ?: local.year,
        status = network.status ?: local.status,
        ratings = if (network.ratings > 0) network.ratings else local.ratings,
        vote_count = if (network.vote_count > 0) network.vote_count else local.vote_count,
        genres = network.genres ?: local.genres,
        poster_path = network.poster_path ?: local.poster_path,
        backdrop_path = network.backdrop_path ?: local.backdrop_path,
        episode_numbers = network.episode_numbers ?: local.episode_numbers,
        season_numbers = network.season_numbers ?: local.season_numbers,
    )
}
