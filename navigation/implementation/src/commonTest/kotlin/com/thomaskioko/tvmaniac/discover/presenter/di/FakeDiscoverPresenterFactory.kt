package com.thomaskioko.tvmaniac.discover.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.domain.discover.DiscoverShowsInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import kotlinx.coroutines.test.StandardTestDispatcher

class FakeDiscoverPresenterFactory : DiscoverShowsPresenter.Factory {

    private val featuredShowsRepository = FakeFeaturedShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()
    private val popularShowsRepository = FakePopularShowsRepository()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val genreRepository = FakeGenreRepository()
    private val watchlistRepository = FakeWatchlistRepository()
    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    override fun create(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToMore: (categoryId: Long) -> Unit,
    ): DiscoverShowsPresenter = DiscoverShowsPresenter(
        componentContext = componentContext,
        onNavigateToShowDetails = onNavigateToShowDetails,
        onNavigateToMore = onNavigateToMore,
        discoverShowsInteractor = DiscoverShowsInteractor(
            featuredShowsRepository = featuredShowsRepository,
            topRatedShowsRepository = topRatedShowsRepository,
            popularShowsRepository = popularShowsRepository,
            trendingShowsRepository = trendingShowsRepository,
            upcomingShowsRepository = upcomingShowsRepository,
            genreRepository = genreRepository,
            dispatchers = coroutineDispatcher,
        ),
        watchlistRepository = watchlistRepository,
        featuredShowsInteractor = FeaturedShowsInteractor(
            featuredShowsRepository = featuredShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        topRatedShowsInteractor = TopRatedShowsInteractor(
            topRatedShowsRepository = topRatedShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        popularShowsInteractor = PopularShowsInteractor(
            popularShowsRepository = popularShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        trendingShowsInteractor = TrendingShowsInteractor(
            trendingShowsRepository = trendingShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        upcomingShowsInteractor = UpcomingShowsInteractor(
            upcomingShowsRepository = upcomingShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        genreShowsInteractor = GenreShowsInteractor(
            repository = genreRepository,
            dispatchers = coroutineDispatcher,
        ),
        logger = FakeLogger(),
    )
}
