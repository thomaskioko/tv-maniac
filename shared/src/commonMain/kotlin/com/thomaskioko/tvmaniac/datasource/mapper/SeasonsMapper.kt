package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse

fun ShowDetailResponse.toSeasonsEntityList(): List<SeasonsEntity> {
    return seasons.map { seasonResponse ->
        SeasonsEntity(
            tvShowId = id,
            seasonId = seasonResponse.id,
            name = seasonResponse.name,
            overview = seasonResponse.overview,
            seasonNumber = seasonResponse.seasonNumber,
            episodeCount = seasonResponse.episodeCount
        )
    }
}

fun List<SelectSeasonsByShowId>.toSeasonsEntityList(): List<SeasonsEntity> {
    return map { it.toSeasonsEntity() }
}

fun SelectSeasonsByShowId.toSeasonsEntity(): SeasonsEntity {
    return SeasonsEntity(
        seasonId = season_id.toInt(),
        tvShowId = tv_show_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = season_number.toInt(),
        episodeCount = epiosode_count.toInt()
    )
}

fun Season.toSeasonEntity(): SeasonsEntity {
    return SeasonsEntity(
        seasonId = season_id.toInt(),
        tvShowId = tv_show_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = season_number.toInt(),
        episodeCount = epiosode_count.toInt()
    )
}
