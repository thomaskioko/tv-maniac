package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.presentation.model.SeasonUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class SeasonsInteractor constructor(
    private val repository: SeasonsRepository,
) : FlowInteractor<Int, List<SeasonUiModel>>() {

    override fun run(params: Int): Flow<List<SeasonUiModel>> = flow {

        emit(repository.getSeasonListByTvShowId(params))
    }
        .distinctUntilChanged()
}
