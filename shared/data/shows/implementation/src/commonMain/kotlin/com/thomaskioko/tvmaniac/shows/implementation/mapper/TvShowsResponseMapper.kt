package com.thomaskioko.tvmaniac.shows.implementation.mapper

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResponse
import kotlinx.datetime.toLocalDate

fun TmdbShowResponse.toShow(): Show {
    return Show(
        tmdb_id = id.toLong(),
        trakt_id = id.toLong(),
        title = name,
        overview = overview,
        language = originalLanguage,
        votes = voteCount.toLong(),
        genres = genreIds.map { it.toString() },
        year = formatDate(firstAirDate),
        aired_episodes = numberOfEpisodes?.toLong(),
        rating = popularity,
        status = "",
        runtime = 0
    )
}

private fun formatDate(dateString: String): String {
    return if (dateString.isNotBlank() && !dateString.contains("N/A"))
        dateString.toLocalDate().year.toString()
    else
        dateString
}
