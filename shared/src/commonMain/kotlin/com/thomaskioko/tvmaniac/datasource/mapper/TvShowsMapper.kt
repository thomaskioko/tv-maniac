package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Tv_show
import com.thomaskioko.tvmaniac.datasource.network.model.ShowResponse
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.StringUtil.formatPosterPath
import kotlinx.datetime.toLocalDate

fun ShowResponse.toTvShowEntity(): TvShow {
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

private fun formatDate(dateString: String) =  dateString.toLocalDate().year.toString()

fun List<Tv_show>.toTvShowsEntityList(): List<TvShow> {
    return map { it.toTvShowsEntity() }
}

fun Tv_show.toTvShowsEntity(): TvShow {
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
        showCategory = show_category,
        year = year
    )
}
