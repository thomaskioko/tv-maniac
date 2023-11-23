package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse

fun List<TraktSeasonsResponse>.toSeasonCacheList(traktId: Long): List<Season> =
    map { seasonResponse ->
        Season(
            show_id = Id(traktId),
            id = Id(id = seasonResponse.ids.trakt.toLong()),
            season_number = seasonResponse.number.toLong(),
            title = seasonResponse.title,
            overview = seasonResponse.overview,
            episode_count = seasonResponse.episodeCount.toLong(),
        )
    }
