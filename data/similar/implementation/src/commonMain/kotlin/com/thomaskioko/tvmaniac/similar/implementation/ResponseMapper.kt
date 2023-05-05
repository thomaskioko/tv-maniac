package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse

fun TraktShowResponse.toShow(): Show {
    return Show(
        trakt_id = ids.trakt.toLong(),
        tmdb_id = ids.tmdb?.toLong(),
        title = title,
        overview = overview ?: "",
        language = language,
        votes = votes.toLong(),
        rating = rating,
        genres = genres,
        year = year ?: "--",
        status = status,
        aired_episodes = airedEpisodes.toLong(),
        runtime = runtime.toLong(),
    )
}
