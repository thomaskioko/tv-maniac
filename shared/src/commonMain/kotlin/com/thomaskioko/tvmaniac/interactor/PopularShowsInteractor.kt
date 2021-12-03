package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShow
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class PopularShowsInteractor constructor(
    private val repository: TvShowsRepository,
) : Interactor<Unit, List<TvShow>>() {
    override fun run(params: Unit): Flow<DomainResultState<List<TvShow>>> = flow {
        emit(loading())

        val result = repository.getShowsByCategoryId(1, ShowCategory.POPULAR.type)
            .map { it.toTvShow() }

        emit(success(result))
    }
        .catch { emit(error(it)) }
}
