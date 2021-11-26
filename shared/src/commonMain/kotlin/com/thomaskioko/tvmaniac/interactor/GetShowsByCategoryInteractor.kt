package com.thomaskioko.tvmaniac.interactor

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.THIS_WEEK
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TODAY
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.CommonFlow
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.error
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Companion.success
import com.thomaskioko.tvmaniac.util.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class GetShowsByCategoryInteractor constructor(
    private val repository: TvShowsRepository,
) : Interactor<Int, CommonFlow<PagingData<TvShow>>>() {

    override fun run(params: Int): Flow<DomainResultState<CommonFlow<PagingData<TvShow>>>> =
        flow {
            emit(loading())

            val list = when (val type = ShowCategory[params]) {
                TRENDING, TODAY, THIS_WEEK ->
                    repository
                        .getPagedShowsByCategoryAndWindow(TRENDING, type.timeWindow!!)
                POPULAR -> repository.getPagedPopularTvShows()
                TOP_RATED -> repository.getPagedTopRatedTvShows()
                else -> repository.getPagedShowsByCategory(type)
            }

            emit(success(list))
        }
            .distinctUntilChanged()
            .catch { emit(error(it)) }
}
