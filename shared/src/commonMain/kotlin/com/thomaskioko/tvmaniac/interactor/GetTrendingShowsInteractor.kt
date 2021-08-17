package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.THIS_WEEK
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TODAY
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
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

class GetTrendingShowsInteractor constructor(
    private val repository: TvShowsRepository,
) : Interactor<List<ShowCategory>, LinkedHashMap<ShowCategory, List<TvShow>>>() {

    override fun run(params: List<ShowCategory>): Flow<DomainResultState<LinkedHashMap<ShowCategory, List<TvShow>>>> =
        flow {
            emit(loading())

            val trendingMap = linkedMapOf<ShowCategory, List<TvShow>>()

            params.forEach {
                val result = when (it) {
                    FEATURED -> repository.getFeaturedShows()
                    TODAY, THIS_WEEK -> repository.getTrendingShowsByTime(it.timeWindow!!)
                    POPULAR -> repository.getPopularTvShows(1)
                    TOP_RATED -> repository.getTopRatedTvShows(1)
                    TRENDING -> repository.getShowsByCategory(TRENDING)
                }

                trendingMap[it] = result
            }

            emit(success(trendingMap))
        }
            .catch { emit(error(it)) }
}