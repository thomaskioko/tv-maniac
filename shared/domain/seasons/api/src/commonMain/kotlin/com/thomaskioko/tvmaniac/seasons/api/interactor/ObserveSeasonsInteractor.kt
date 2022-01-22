package com.thomaskioko.tvmaniac.seasons.api.interactor

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveSeasonsInteractor constructor(
    private val repository: SeasonsRepository,
) : FlowInteractor<Int, List<SeasonUiModel>>() {

    override fun run(params: Int): Flow<List<SeasonUiModel>> =
        repository.observeShowSeasons(params)
            .map { it.data?.toSeasonsEntityList() ?: emptyList() }
            .distinctUntilChanged()
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
