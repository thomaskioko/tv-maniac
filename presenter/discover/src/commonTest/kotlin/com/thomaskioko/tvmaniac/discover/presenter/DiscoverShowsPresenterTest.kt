package com.thomaskioko.tvmaniac.discover.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
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
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.domain.discover.DiscoverShowsInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DiscoverShowsPresenterTest {

    private val testDispatcher = StandardTestDispatcher()

    private val featuredShowsRepository = FakeFeaturedShowsRepository()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()
    private val popularShowsRepository = FakePopularShowsRepository()
    private val watchlistRepository = FakeWatchlistRepository()
    private val genreRepository = FakeGenreRepository()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var presenter: DiscoverShowsPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)

        presenter = buildPresenter()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `instance is maintained across recreations`() = runTest {
        // Create the first instance
        val initialPresenter = buildPresenter()

        // Simulate some state change
        initialPresenter.dispatch(RefreshData)

        val recreatedPresenter = buildPresenter()

        // Assert that the states are the same
        initialPresenter.state.value shouldBeEqual recreatedPresenter.state.value

        recreatedPresenter.dispatch(PopularClicked)

        // Assert that the internal PresenterInstance is the same
        initialPresenter.presenterInstance shouldNotBeSameInstanceAs recreatedPresenter.presenterInstance
    }

    @Test
    fun `should return EmptyState when data is empty`() = runTest {
        presenter.state.test {
            setList(emptyList())

            awaitItem() shouldBe DiscoverViewState.Empty
        }
    }

    @Test
    fun `should return DataLoaded when data is fetched`() = runTest {
        presenter.state.test {
            setList(emptyList())

            awaitItem() shouldBe DiscoverViewState.Empty

            setList(createDiscoverShowList())

            awaitItem() shouldBe DiscoverViewState(
                featuredShows = uiModelList(),
                topRatedShows = uiModelList(),
                popularShows = uiModelList(),
                upcomingShows = uiModelList(),
                trendingToday = uiModelList(),
            )
        }
    }

    @Test
    fun `should return DataLoaded when data is fetched from cache`() = runTest {
        presenter.state.test {
            setList(createDiscoverShowList())

            awaitItem() shouldBe DiscoverViewState.Empty
            awaitItem() shouldBe DiscoverViewState(
                featuredShows = uiModelList(),
                topRatedShows = uiModelList(),
                popularShows = uiModelList(),
                upcomingShows = uiModelList(),
                trendingToday = uiModelList(),
            )
        }
    }

    @Test
    fun `should return DataLoaded when data is empty and refresh is clicked`() = runTest {
        presenter.state.test {
            setList(emptyList())

            awaitItem() shouldBe DiscoverViewState.Empty

            presenter.dispatch(RefreshData)

            setList(createDiscoverShowList())

            awaitItem() shouldBe DiscoverViewState(
                featuredShows = uiModelList(),
                topRatedShows = uiModelList(),
                popularShows = uiModelList(),
                upcomingShows = uiModelList(),
                trendingToday = uiModelList(),
            )
        }
    }

    @Test
    fun `should return DataLoaded with refreshed data when refresh is clicked`() = runTest {
        presenter.state.test {
            setList(createDiscoverShowList())

            val expectedList = uiModelList()
            val expectedResult = DiscoverViewState(
                featuredShows = expectedList,
                topRatedShows = expectedList,
                popularShows = expectedList,
                upcomingShows = expectedList,
                trendingToday = expectedList,
            )

            awaitItem() shouldBe DiscoverViewState.Empty
            awaitItem() shouldBe expectedResult

            presenter.dispatch(RefreshData)

            awaitItem() shouldBe expectedResult.copy(
                featuredRefreshing = true,
                topRatedRefreshing = true,
                upcomingRefreshing = true,
                popularRefreshing = true,
                trendingRefreshing = true,
            )

            setList(createDiscoverShowList())

            val expectedUpdatedList = uiModelList()

            awaitItem() shouldBe DiscoverViewState(
                featuredShows = expectedUpdatedList,
                topRatedShows = expectedUpdatedList,
                popularShows = expectedUpdatedList,
                upcomingShows = expectedUpdatedList,
                trendingToday = expectedUpdatedList,
            )
        }
    }

    @Test
    fun `should return DataLoaded when error occurs and refresh is clicked`() = runTest {
        setList(createDiscoverShowList())

        presenter.state.test {
            awaitItem() shouldBe DiscoverViewState.Empty

            val expectedList = uiModelList()
            val expectedResult = DiscoverViewState(
                featuredShows = expectedList,
                topRatedShows = expectedList,
                popularShows = expectedList,
                upcomingShows = expectedList,
                trendingToday = expectedList,
            )

            awaitItem() shouldBe expectedResult

            presenter.dispatch(RefreshData)

            awaitItem() shouldBe expectedResult.copy(
                featuredRefreshing = true,
                topRatedRefreshing = true,
                upcomingRefreshing = true,
                popularRefreshing = true,
                trendingRefreshing = true,
            )

            setList(createDiscoverShowList())

            val expectedUpdatedList = uiModelList()

            val expectedUpdatedResult = DiscoverViewState(
                featuredShows = expectedUpdatedList,
                topRatedShows = expectedUpdatedList,
                popularShows = expectedUpdatedList,
                upcomingShows = expectedUpdatedList,
                trendingToday = expectedUpdatedList,
                featuredRefreshing = false,
                message = null,
            )

            awaitItem() shouldBe expectedUpdatedResult
        }
    }

    private suspend fun setList(list: List<ShowEntity>) {
        featuredShowsRepository.setFeaturedShows(list)
        topRatedShowsRepository.setTopRatedShows(list)
        popularShowsRepository.setPopularShows(list)
        upcomingShowsRepository.setUpcomingShows(list)
        trendingShowsRepository.setTrendingShows(list)
        genreRepository.setGenreResult(emptyList())
    }

    private fun createDiscoverShowList(size: Int = LIST_SIZE) = List(size) {
        ShowEntity(
            id = 84958,
            title = "Loki",
            posterPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            inLibrary = false,
        )
    }.toImmutableList()

    private fun uiModelList(size: Int = LIST_SIZE) = createDiscoverShowList(size).map {
        DiscoverShow(
            tmdbId = it.id,
            title = it.title,
            posterImageUrl = it.posterPath,
            inLibrary = it.inLibrary,
            overView = it.overview,
        )
    }.toImmutableList()

    companion object {
        const val LIST_SIZE = 5
    }

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
    ): DiscoverShowsPresenter = DiscoverShowsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        onNavigateToShowDetails = {},
        onNavigateToMore = {},
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
    ).also { lifecycle.resume() }
}
