package com.thomaskioko.tvmaniac.domain.discover

import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.genre.GenreRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

@Inject
class DiscoverShowsInteractor(
    private val featuredShowsRepository: FeaturedShowsRepository,
    private val topRatedShowsRepository: TopRatedShowsRepository,
    private val popularShowsRepository: PopularShowsRepository,
    private val trendingShowsRepository: TrendingShowsRepository,
    private val upcomingShowsRepository: UpcomingShowsRepository,
    private val genreRepository: GenreRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Unit, DiscoverShowsData>() {

    override fun createObservable(params: Unit): Flow<DiscoverShowsData> = combine(
        genreRepository.observeGenresWithShows(),
        featuredShowsRepository.observeFeaturedShows(),
        topRatedShowsRepository.observeTopRatedShows(),
        popularShowsRepository.observePopularShows(),
        trendingShowsRepository.observeTrendingShows(),
        upcomingShowsRepository.observeUpcomingShows(),
    ) { _, featured, topRated, popular, trending, upcoming ->
        DiscoverShowsData(
            featuredShows = featured,
            topRatedShows = topRated,
            popularShows = popular,
            trendingShows = trending,
            upcomingShows = upcoming,
        )
    }.flowOn(dispatchers.io.limitedParallelism(6))
}

data class DiscoverShowsData(
    val featuredShows: List<ShowEntity>,
    val topRatedShows: List<ShowEntity>,
    val popularShows: List<ShowEntity>,
    val trendingShows: List<ShowEntity>,
    val upcomingShows: List<ShowEntity>,
)
