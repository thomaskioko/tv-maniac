package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse

fun List<SelectShows>.toShowList(): List<Show> {
    return map { it.toShow() }
}

fun SelectShows.toShow(): Show {
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

fun TraktShowsResponse.toShow(): Show {
    return Show(
        trakt_id = show.ids.trakt,
        tmdb_id = show.ids.tmdb,
        title = show.title,
        overview = show.overview,
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

fun TraktShowResponse.toShow(): Show {
    return Show(
        trakt_id = ids.trakt,
        tmdb_id = ids.tmdb,
        title = title,
        overview = overview,
        votes = votes,
        year = year,
        aired_episodes = airedEpisodes,
        runtime = runtime,
        language = language?.uppercase(),
        rating = rating.toTwoDecimalPoint(),
        genres = genres.map { it.replaceFirstChar { it.uppercase() } },
        status = status.replaceFirstChar { it.uppercase() },
        poster_image_url = null,
        backdrop_image_url = null
    )
}

fun String?.toImageUrl() = FormatterUtil.formatPosterPath(this)

fun Double?.toTwoDecimalPoint() = FormatterUtil.formatDouble(this, 1)