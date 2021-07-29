package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetShowInteractor constructor(
    private val repository: TvShowsRepository,
) : Interactor<Int, TvShow>() {

    override fun run(params: Int): Flow<DomainResultState<TvShow>> = flow {
        emit(loading())

        emit(success(repository.getTvShow(params)))
    }
        .catch { emit(error(it)) }
}