package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Tvshow

public fun mergeShows(
    local: Tvshow?,
    network: ShowToPersist,
): ShowToPersist {
    if (local == null) return network

    return ShowToPersist(
        showId = network.showId,
        tmdbId = network.tmdbId,
        name = network.name,
        overview = network.overview.ifEmpty { local.overview },
        language = network.language ?: local.language,
        year = network.year ?: local.year,
        status = network.status ?: local.status,
        ratings = if (network.ratings > 0) network.ratings else local.ratings,
        voteCount = if (network.voteCount > 0) network.voteCount else local.vote_count,
        genres = network.genres ?: local.genres,
        posterPath = network.posterPath ?: local.poster_path,
        backdropPath = network.backdropPath ?: local.backdrop_path,
        episodeNumbers = network.episodeNumbers ?: local.episode_numbers,
        seasonNumbers = network.seasonNumbers ?: local.season_numbers,
    )
}
