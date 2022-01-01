package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepository
import com.thomaskioko.tvmaniac.presentation.model.TrailerModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTrailersInteractor constructor(
    private val repository: TrailerRepository,
) : FlowInteractor<Int, List<TrailerModel>>() {

    override fun run(params: Int): Flow<List<TrailerModel>> = flow {

        emit(repository.getTrailers(params))
    }
}
