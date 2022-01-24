package com.thomaskioko.tvmaniac.lastairepisodes.api

import com.thomaskioko.tvmaniac.datasource.cache.AirEpisodesByShowId

fun List<AirEpisodesByShowId>.toLastAirEpisodeList(): List<LastAirEpisode> = map {
    LastAirEpisode(
        id = it.id,
        name = "S${it.season_number}.E${it.episode_number} • ${it.name}",
        overview = it.overview,
        airDate = it.air_date,
        episodeNumber = it.episode_number,
        seasonNumber = it.season_number,
        posterPath = it.still_path,
        voteAverage = it.vote_average,
        voteCount = it.vote_count,
        title = it.title
    )
}
