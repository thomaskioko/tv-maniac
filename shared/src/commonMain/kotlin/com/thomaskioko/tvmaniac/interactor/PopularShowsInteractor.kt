package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.TvShowsRepository
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
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
) : Interactor<Unit, List<TvShowsEntity>>() {
    override fun run(params: Unit): Flow<DomainResultState<List<TvShowsEntity>>> = flow {
        emit(loading())

        emit(success(repository.getPopularTvShows(1)))
    }
        .catch { emit(error(it)) }
}