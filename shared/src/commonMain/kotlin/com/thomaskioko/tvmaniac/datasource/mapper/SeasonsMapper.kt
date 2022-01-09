package com.thomaskioko.tvmaniac.datasource.mapper

import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.presentation.model.SeasonUiModel
import com.thomaskioko.tvmaniac.remote.api.model.ShowDetailResponse

fun ShowDetailResponse.toSeasonCacheList(): List<Season> {
    return seasons.map { seasonResponse ->
        Season(
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

fun List<SelectSeasonsByShowId>.toSeasonsEntityList(): List<SeasonUiModel> {
    return map { it.toSeasonsEntity() }
}

fun SelectSeasonsByShowId.toSeasonsEntity(): SeasonUiModel {
    return SeasonUiModel(
        seasonId = id.toInt(),
        tvShowId = tv_show_id.toInt(),
        name = name,
        overview = overview,
        seasonNumber = season_number.toInt(),
        episodeCount = epiosode_count.toInt()
    )
}
