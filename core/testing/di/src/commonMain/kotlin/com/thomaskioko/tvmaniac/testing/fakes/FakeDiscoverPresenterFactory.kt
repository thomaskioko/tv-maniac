package com.thomaskioko.tvmaniac.testing.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.domain.discover.DiscoverShowsInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.testing.TestScope
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class)
class FakeDiscoverPresenterFactory(
    private val discoverShowsInteractor: DiscoverShowsInteractor,
    private val watchlistRepository: WatchlistRepository,
    private val featuredShowsInteractor: FeaturedShowsInteractor,
    private val topRatedShowsInteractor: TopRatedShowsInteractor,
    private val popularShowsInteractor: PopularShowsInteractor,
    private val trendingShowsInteractor: TrendingShowsInteractor,
    private val upcomingShowsInteractor: UpcomingShowsInteractor,
    private val genreShowsInteractor: GenreShowsInteractor,
    private val logger: Logger,
) : DiscoverShowsPresenter.Factory {
    override fun create(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (Long) -> Unit,
        onNavigateToMore: (Long) -> Unit,
    ): DiscoverShowsPresenter = DiscoverShowsPresenter(
        componentContext = componentContext,
        onNavigateToShowDetails = onNavigateToShowDetails,
        onNavigateToMore = onNavigateToMore,
        discoverShowsInteractor = discoverShowsInteractor,
        watchlistRepository = watchlistRepository,
        featuredShowsInteractor = featuredShowsInteractor,
        topRatedShowsInteractor = topRatedShowsInteractor,
        popularShowsInteractor = popularShowsInteractor,
        trendingShowsInteractor = trendingShowsInteractor,
        upcomingShowsInteractor = upcomingShowsInteractor,
        genreShowsInteractor = genreShowsInteractor,
        logger = logger,
    )
}
