package com.thomaskioko.tvmanic.trakt.implementation.mapper

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse

fun TraktShowResponse.toShow(): Show {
    return Show(
        trakt_id = ids.trakt,
        tmdb_id = ids.tmdb,
        title = title,
        overview = overview ?: "",
        votes = votes,
        year = year,
        runtime = runtime,
        aired_episodes = airedEpisodes,
        language = language?.uppercase(),
        rating = rating.toTwoDecimalPoint(),
        genres = genres.map { it.replaceFirstChar { it.uppercase() } },
        status = status.replaceFirstChar { it.uppercase() },
        poster_image_url = null,
        backdrop_image_url = null
    )
}

fun TraktShowsResponse.toShow(): Show {
    return Show(
        trakt_id = show.ids.trakt,
        tmdb_id = show.ids.tmdb,
        title = show.title,
        overview = show.overview ?: "",
        votes = show.votes,
        year = show.year,
        runtime = show.runtime,
        aired_episodes = show.airedEpisodes,
        language = show.language?.uppercase(),
        rating = show.rating.toTwoDecimalPoint(),
        genres = show.genres.map { it.replaceFirstChar { it.uppercase() } },
        status = show.status.replaceFirstChar { it.uppercase() },
        poster_image_url = null,
        backdrop_image_url = null
    )
}

fun Double?.toTwoDecimalPoint() = FormatterUtil.formatDouble(this, 1)