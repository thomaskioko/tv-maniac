package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.mapper.toTvShow
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetShowInteractor constructor(
    private val repository: TvShowsRepository,
) : Interactor<Int, TvShow>() {

    override fun run(params: Int): Flow<DomainResultState<TvShow>> = repository.getTvShow(params)
        .map { it.toTvShow() }
        .map { success(it) }
        .catch { emit(error(it)) }
}
