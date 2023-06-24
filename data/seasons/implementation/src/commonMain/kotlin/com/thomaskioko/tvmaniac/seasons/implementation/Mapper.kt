package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse

fun List<TraktSeasonsResponse>.toSeasonCacheList(traktId: Long): List<Seasons> =
    map { seasonResponse ->
        Seasons(
            show_trakt_id = traktId,
            id = seasonResponse.ids.trakt.toLong(),
            season_number = seasonResponse.number.toLong(),
            name = seasonResponse.title,
            overview = seasonResponse.overview,
            episode_count = seasonResponse.episodeCount.toLong(),
        )
    }
