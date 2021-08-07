package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.datasource.enums.TvShowType
import com.thomaskioko.tvmaniac.datasource.enums.TvShowType.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.TvShowType.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.TvShowType.THIS_WEEK
import com.thomaskioko.tvmaniac.datasource.enums.TvShowType.TODAY
import com.thomaskioko.tvmaniac.datasource.enums.TvShowType.TOP_RATED
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
) : Interactor<List<TvShowType>, LinkedHashMap<TvShowType, List<TvShow>>>() {

    override fun run(params: List<TvShowType>): Flow<DomainResultState<LinkedHashMap<TvShowType, List<TvShow>>>> =
        flow {
            emit(loading())

            val trendingMap = linkedMapOf<TvShowType, List<TvShow>>()

            params.forEach {
                val result = when (it) {
                    FEATURED -> repository.getFeaturedShows()
                    TODAY, THIS_WEEK -> repository.getTrendingShows(it.timeWindow.window)
                    POPULAR -> repository.getPopularTvShows(1)
                    TOP_RATED -> repository.getTopRatedTvShows(1)
                }

                trendingMap[it] = result
            }

            emit(success(trendingMap))
        }
            .catch { emit(error(it)) }
}