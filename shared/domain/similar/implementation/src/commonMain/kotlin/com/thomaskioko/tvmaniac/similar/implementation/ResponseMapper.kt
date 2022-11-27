package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

fun TraktShowResponse.toShow(): Show {
    return Show(
        trakt_id = ids.trakt,
        tmdb_id = ids.tmdb,
        title = title,
        overview = overview ?: "",
        language = language,
        votes = votes,
        rating = rating,
        genres = genres,
        year = year ?: "--",
        status = status,
        aired_episodes = airedEpisodes,
        runtime = runtime,
    )
}