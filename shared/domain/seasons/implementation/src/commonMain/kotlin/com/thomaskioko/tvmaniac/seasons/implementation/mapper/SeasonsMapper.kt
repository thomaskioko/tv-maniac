package com.thomaskioko.tvmaniac.seasons.implementation.mapper

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse

fun List<TraktSeasonsResponse>.toSeasonCacheList(traktId : Int): List<Season> {
    return map { seasonResponse ->
        Season(
            id = seasonResponse.ids.trakt,
            trakt_id = traktId,
            season_number = seasonResponse.number,
            name = seasonResponse.title,
            overview = seasonResponse.overview,
            epiosode_count = seasonResponse.episodeCount
        )
    }
}
