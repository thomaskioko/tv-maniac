package com.thomaskioko.tvmaniac.shows.implementation.mapper

import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.FormatterUtil

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
        poster_image_url = poster_image_url.toImageUrl(),
        backdrop_image_url = backdrop_image_url.toImageUrl()
    )
}

fun String?.toImageUrl() = FormatterUtil.formatPosterPath(this)