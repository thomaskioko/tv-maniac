package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.mapper.toSeasonsEntityList
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.presentation.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class SeasonsInteractor constructor(
    private val repository: SeasonsRepository,
) : FlowInteractor<Int, List<SeasonUiModel>>() {

    override fun run(params: Int): Flow<List<SeasonUiModel>> =
        repository.observeShowSeasons(params)
            .map { it.data?.toSeasonsEntityList() ?: emptyList() }
            .distinctUntilChanged()
}
