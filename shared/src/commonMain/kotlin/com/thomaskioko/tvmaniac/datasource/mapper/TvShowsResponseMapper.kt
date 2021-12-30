package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.network.model.GenreResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowResponse
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.StringUtil
import kotlinx.datetime.toLocalDate

fun ShowResponse.toShow(): Show {
    return Show(
        id = id.toLong(),
        title = name,
        description = overview,
        language = originalLanguage,
        poster_image_url = StringUtil.formatPosterPath(posterPath),
        backdrop_image_url = if (backdropPath.isNullOrEmpty()) StringUtil.formatPosterPath(posterPath) else StringUtil.formatPosterPath(
            backdropPath
        ),
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
        poster_image_url = StringUtil.formatPosterPath(posterPath),
        backdrop_image_url = if (backdropPath.isEmpty()) StringUtil.formatPosterPath(posterPath) else StringUtil.formatPosterPath(
            backdropPath
        ),
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

fun ShowResponse.toTvShow(): TvShow {
    return TvShow(
        id = id,
        title = name,
        overview = overview,
        language = originalLanguage,
        posterImageUrl = StringUtil.formatPosterPath(posterPath),
        backdropImageUrl = if (backdropPath.isNullOrEmpty()) StringUtil.formatPosterPath(posterPath) else StringUtil.formatPosterPath(
            backdropPath
        ),
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
