package com.thomaskioko.tvmaniac.discover.implementation.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.discover.api.model.ShowUiModel
import com.thomaskioko.tvmaniac.remote.api.model.GenreResponse
import com.thomaskioko.tvmaniac.remote.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.remote.api.model.ShowResponse
import com.thomaskioko.tvmaniac.shared.core.util.StringUtil.formatPosterPath
import kotlinx.datetime.toLocalDate

fun ShowResponse.toShow(): Show {
    return Show(
        id = id.toLong(),
        title = name,
        description = overview,
        language = originalLanguage,
        poster_image_url = formatPosterPath(posterPath),
        backdrop_image_url = backdropPath.toImageUrl(posterPath),
        votes = voteCount.toLong(),
        vote_average = voteAverage,
        genre_ids = genreIds,
        year = formatDate(firstAirDate),
        season_ids = null,
        status = "",
        popularity = popularity,
        is_watchlist = false
    )
}

fun ShowDetailResponse.toShow(): Show {
    return Show(
        id = id.toLong(),
        title = name,
        description = overview,
        language = originalLanguage,
        poster_image_url = formatPosterPath(posterPath),
        backdrop_image_url = backdropPath.toImageUrl(posterPath),
        votes = voteCount.toLong(),
        vote_average = voteAverage,
        genre_ids = genres.toGenreIds(),
        year = formatDate(firstAirDate),
        season_ids = null,
        status = status,
        popularity = popularity,
        is_watchlist = false
    )
}

fun List<GenreResponse>.toGenreIds(): List<Int> = map { it.id }

fun ShowResponse.toTvShow(): ShowUiModel {
    return ShowUiModel(
        id = id,
        title = name,
        overview = overview,
        language = originalLanguage,
        posterImageUrl = formatPosterPath(posterPath),
        backdropImageUrl = backdropPath.toImageUrl(posterPath),
        votes = voteCount,
        averageVotes = voteAverage,
        genreIds = genreIds,
        year = formatDate(firstAirDate)
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
