package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.datasource.cache.Season as SeasonCache

fun ShowDetailResponse.toSeasonsEntityList(): List<Season> {
    return seasons.map { seasonResponse ->
        Season(
            tvShowId = id,
            seasonId = seasonResponse.id,
            name = seasonResponse.name,
            overview = seasonResponse.overview,
            seasonNumber = seasonResponse.seasonNumber,
            episodeCount = seasonResponse.episodeCount
        )
    }
}

fun List<SelectSeasonsByShowId>.toSeasonsEntityList(): List<Season> {
    return map { it.toSeasonsEntity() }
}

fun SelectSeasonsByShowId.toSeasonsEntity(): Season {
    return Season(
        seasonId = id.toInt(),
        tvShowId = tv_show_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = season_number.toInt(),
        episodeCount = epiosode_count.toInt()
    )
}

fun SeasonCache.toSeasonEntity(): Season {
    return Season(
        seasonId = id.toInt(),
        tvShowId = tv_show_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = season_number.toInt(),
        episodeCount = epiosode_count.toInt()
    )
}
