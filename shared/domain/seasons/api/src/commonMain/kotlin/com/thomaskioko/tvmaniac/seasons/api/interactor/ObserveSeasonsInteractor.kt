package com.thomaskioko.tvmaniac.seasons.api.interactor

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveSeasonsInteractor constructor(
    private val repository: SeasonsRepository,
) : FlowInteractor<Long, List<SeasonUiModel>>() {

    override fun run(params: Long): Flow<List<SeasonUiModel>> =
        repository.observeShowSeasons(params)
            .map { it.data?.toSeasonsEntityList() ?: emptyList() }
            .distinctUntilChanged()
}

fun List<SelectSeasonsByShowId>.toSeasonsEntityList(): List<SeasonUiModel> {
    return map { it.toSeasonsEntity() }
}

fun SelectSeasonsByShowId.toSeasonsEntity(): SeasonUiModel {
    return SeasonUiModel(
        seasonId = id,
        tvShowId = tv_show_id,
        name = name,
        overview = overview,
        seasonNumber = season_number,
        episodeCount = epiosode_count.toInt()
    )
}
