package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

fun List<TraktShowResponse>.responseToShow(): List<SimilarShows> {
    return map {
        SimilarShows(
            trakt_id = it.ids.trakt.toLong(),
            tmdb_id = it.ids.tmdb?.toLong(),
            title = it.title,
            overview = it.overview ?: "",
            language = it.language,
            votes = it.votes.toLong(),
            rating = it.rating,
            genres = it.genres,
            year = it.year?.toString() ?: "--",
            status = it.status,
            runtime = it.runtime.toLong(),
            poster_url = null,
            backdrop_url = null,
        )
    }
}

fun SimilarShows.toShow(): Show {
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
        aired_episodes = 0,
        runtime = runtime,
    )
}
