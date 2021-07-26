package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Tv_season
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.presentation.model.Season

fun ShowDetailResponse.toSeasonCacheList(): List<Tv_season> {
    return seasons.map { seasonResponse ->
        Tv_season(
            id = seasonResponse.id.toLong(),
            tv_show_id = id.toLong(),
            season_number = seasonResponse.seasonNumber.toLong(),
            name = seasonResponse.name,
            overview = seasonResponse.overview,
            epiosode_count = seasonResponse.episodeCount.toLong(),
            episode_ids = null
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

fun Tv_season.toSeasonEntity(): Season {
    return Season(
        seasonId = id.toInt(),
        tvShowId = tv_show_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = season_number.toInt(),
        episodeCount = epiosode_count.toInt()
    )
}
