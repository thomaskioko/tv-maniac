package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.DateUtil.formatDateString
import com.thomaskioko.tvmaniac.core.util.StringUtil
import com.thomaskioko.tvmaniac.remote.api.model.ShowResponse

fun ShowResponse.toShow(): Show {
    return Show(
        id = id.toLong(),
        title = name,
        description = overview,
        language = originalLanguage,
        poster_image_url = StringUtil.formatPosterPath(posterPath),
        backdrop_image_url = backdropPath.toImageUrl(posterPath),
        votes = voteCount.toLong(),
        vote_average = voteAverage,
        genre_ids = genreIds,
        year = formatDateString(dateString = firstAirDate),
        status = "",
        popularity = popularity,
        following = false,
        number_of_seasons = numberOfSeasons?.toLong(),
        number_of_episodes = numberOfEpisodes?.toLong()
    )
}

private fun String?.toImageUrl(posterPath: String?) =
    if (this.isNullOrEmpty()) StringUtil.formatPosterPath(posterPath)
    else StringUtil.formatPosterPath(this)
