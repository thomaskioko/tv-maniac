package com.thomaskioko.tvmanic.trakt.implementation.mapper

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

fun TraktShowResponse.toShow(): Show {
    return Show(
        trakt_id = ids.trakt,
        tmdb_id = ids.tmdb,
        title = title,
        overview = overview,
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

fun Double?.toTwoDecimalPoint() = FormatterUtil.formatDouble(this, 1)