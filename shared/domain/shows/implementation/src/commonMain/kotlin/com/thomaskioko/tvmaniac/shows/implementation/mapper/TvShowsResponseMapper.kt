package com.thomaskioko.tvmaniac.shows.implementation.mapper

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.FormatterUtil.formatPosterPath
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResponse
import kotlinx.datetime.toLocalDate

fun TmdbShowResponse.toShow(): Show {
    return Show(
        tmdb_id = id,
        trakt_id = id,
        title = name,
        overview = overview,
        language = originalLanguage,
        poster_image_url = posterPath.toImageUrl(),
        backdrop_image_url = backdropPath.toImageUrl(posterPath),
        votes = voteCount,
        genres = genreIds.map { it.toString() },
        year = formatDate(firstAirDate),
        aired_episodes = numberOfEpisodes,
        rating = popularity,
        status = "",
        runtime = 0
    )
}

fun ShowDetailResponse.toShow(tvShowId: Int): Show {
    return Show(
        tmdb_id = id,
        trakt_id = tvShowId,
        title = name,
        overview = overview,
        language = originalLanguage,
        poster_image_url = formatPosterPath(posterPath),
        backdrop_image_url = backdropPath.toImageUrl(posterPath),
        votes = voteCount,
        genres = genres.map { it.name },
        year = formatDate(firstAirDate),
        aired_episodes = numberOfEpisodes,
        rating = popularity,
        status = status,
        runtime = 0
    )
}

private fun formatDate(dateString: String): String {
    return if (dateString.isNotBlank() && !dateString.contains("N/A"))
        dateString.toLocalDate().year.toString()
    else
        dateString
}

private fun String?.toImageUrl(posterPath: String?) =
    if (this.isNullOrEmpty()) formatPosterPath(posterPath)
    else formatPosterPath(this)
