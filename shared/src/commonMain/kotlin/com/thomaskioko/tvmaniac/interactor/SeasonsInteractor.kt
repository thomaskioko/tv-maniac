package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SeasonsInteractor constructor(
    private val repository: SeasonsRepository,
) : Interactor<Int, List<SeasonsEntity>>() {
    override fun run(params: Int): Flow<DomainResultState<List<SeasonsEntity>>> = flow {
        emit(loading())

        emit(success(repository.getSeasonListByTvShowId(params)))
    }
        .catch { emit(error(it)) }
}