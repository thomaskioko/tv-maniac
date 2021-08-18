package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.THIS_WEEK
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TODAY
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
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

class GetShowsByCategoryInteractor constructor(
    private val repository: TvShowsRepository,
) : Interactor<Int, List<TvShow>>() {

    override fun run(params: Int): Flow<DomainResultState<List<TvShow>>> =
        flow {
            emit(loading())

            val list = when (val type = ShowCategory[params]) {
                TODAY, THIS_WEEK -> repository.getShowsByCategoryAndWindow(TRENDING, type.timeWindow!!)
                else -> repository.getShowsByCategory(type)
            }
            emit(success(list))
        }
            .catch { emit(error(it)) }
}