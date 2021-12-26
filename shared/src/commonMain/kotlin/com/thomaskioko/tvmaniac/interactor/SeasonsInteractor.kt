package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SeasonsInteractor constructor(
    private val repository: SeasonsRepository,
) : FlowInteractor<Int, DomainResultState<List<Season>>>() {

    override fun run(params: Int): Flow<DomainResultState<List<Season>>> = flow {
        emit(loading())

        emit(success(repository.getSeasonListByTvShowId(params)))
    }
        .catch {
            when (it) {
                is NullPointerException ->
                    emit(error(Throwable("No data for season with Show id:: $params")))
                else -> emit(error(it))
            }
        }
}
