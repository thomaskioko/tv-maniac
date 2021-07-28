package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepository
import com.thomaskioko.tvmaniac.presentation.model.TrailerModel
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetTrailersInteractor constructor(
    private val repository: TrailerRepository,
) : Interactor<Int, List<TrailerModel>>() {

    override fun run(params: Int): Flow<DomainResultState<List<TrailerModel>>> = flow {
        emit(loading())

        emit(success(repository.getTrailers(params)))
    }
        .catch { emit(error(it)) }
}