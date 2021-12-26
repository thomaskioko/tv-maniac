package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepository
import com.thomaskioko.tvmaniac.presentation.model.TrailerModel
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetTrailersInteractor constructor(
    private val repository: TrailerRepository,
) : FlowInteractor<Int, DomainResultState<List<TrailerModel>>>() {

    override fun run(params: Int): Flow<DomainResultState<List<TrailerModel>>> = flow {
        emit(loading())

        emit(success(repository.getTrailers(params)))
    }
        .catch { emit(error(it)) }
}
