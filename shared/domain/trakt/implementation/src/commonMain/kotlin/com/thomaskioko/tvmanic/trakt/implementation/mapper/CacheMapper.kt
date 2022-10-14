package com.thomaskioko.tvmanic.trakt.implementation.mapper

import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show

fun List<SelectShowsByCategory>.toShowList(): List<Show> {
    return map { it.toShow() }
}

fun SelectShowsByCategory.toShow(): Show {
    return Show(
        trakt_id = trakt_id,
        tmdb_id = tmdb_id,
        title = title,
        overview = overview,
        language = language,
        votes = votes,
        rating = rating,
        genres = genres,
        year = year,
        status = status,
        aired_episodes = aired_episodes,
        runtime = runtime,
        poster_image_url = poster_image_url,
        backdrop_image_url = poster_image_url
    )
}