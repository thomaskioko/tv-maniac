package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.SelectShows
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.network.model.ShowResponse
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.StringUtil.formatPosterPath
import kotlinx.datetime.toLocalDate

fun ShowResponse.toShow(): Show {
    return Show(
        id = id.toLong(),
        title = name,
        description = overview,
        language = originalLanguage,
        poster_image_url = formatPosterPath(posterPath),
        backdrop_image_url = if (backdropPath.isNullOrEmpty()) formatPosterPath(posterPath) else formatPosterPath(
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

fun ShowResponse.toTvShow(): TvShow {
    return TvShow(
        id = id,
        title = name,
        overview = overview,
        language = originalLanguage,
        posterImageUrl = formatPosterPath(posterPath),
        backdropImageUrl = if (backdropPath.isNullOrEmpty()) formatPosterPath(posterPath) else formatPosterPath(
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

fun List<Show>.toTvShowList(): List<TvShow> {
    return map { it.toTvShow() }
}

fun Show.toTvShow(): TvShow {
    return TvShow(
        id = id.toInt(),
        title = title,
        overview = description,
        language = language,
        posterImageUrl = poster_image_url,
        backdropImageUrl = backdrop_image_url,
        votes = votes.toInt(),
        averageVotes = vote_average,
        genreIds = genre_ids,
        year = year,
        status = status,
        isInWatchlist = is_watchlist
    )
}

fun SelectShows.toTvShow(): Show {
    return Show(
        id = id,
        title = title,
        description = description,
        language = language,
        poster_image_url = poster_image_url,
        backdrop_image_url = backdrop_image_url,
        votes = votes,
        vote_average = vote_average,
        genre_ids = genre_ids,
        year = year,
        status = status,
        is_watchlist = is_watchlist,
        popularity = popularity,
        season_ids = season_ids,
    )
}
