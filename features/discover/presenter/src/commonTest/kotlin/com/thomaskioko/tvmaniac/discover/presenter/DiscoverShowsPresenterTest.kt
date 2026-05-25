package com.thomaskioko.tvmaniac.discover.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.domain.discover.DiscoverShowsInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FollowShowInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.myshows.nav.MyShowsRoot
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.navigation.testing.TestNavigator
import com.thomaskioko.tvmaniac.navigation.testing.test
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DiscoverShowsPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val appCoroutineScope = CoroutineScope(testDispatcher + SupervisorJob())

    private val featuredShowsRepository = FakeFeaturedShowsRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()
    private val popularShowsRepository = FakePopularShowsRepository()
    private val genreRepository = FakeGenreRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val upNextRepository = FakeUpNextRepository()
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()
    private val watchProviderRepository = FakeWatchProviderRepository()
    private val startWatchingRepository = FakeStartWatchingRepository()
    private val fakeLocalizer = FakeLocalizer()
    private val observeUpNextInteractor = ObserveUpNextInteractor(
        repository = upNextRepository,
    )
    private val observeStartWatchingInteractor = ObserveStartWatchingInteractor(
        repository = startWatchingRepository,
    )
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
            setNextEpisodes(emptyList())

            awaitItem() shouldBe DiscoverViewState.Empty

            setList(createDiscoverShowList())
            setNextEpisodes(createNextEpisodesList())

            awaitItem() shouldBe DiscoverViewState(
                isInitial = false,
                featuredShows = uiModelList(),
                topRatedShows = uiModelList(),
                popularShows = uiModelList(),
                upcomingShows = uiModelList(),
                trendingToday = uiModelList(),
                nextEpisodes = nextEpisodeUiModelList(),
                startWatchingTitle = fakeLocalizer.getString(StringResourceKey.LabelStartWatching),
            )
        }
    }

    @Test
    fun `should return DataLoaded when data is fetched from cache`() = runTest {
        presenter.state.test {
            setList(createDiscoverShowList())
            setNextEpisodes(createNextEpisodesList())

            awaitItem() shouldBe DiscoverViewState.Empty
            awaitItem() shouldBe DiscoverViewState(
                isInitial = false,
                featuredShows = uiModelList(),
                topRatedShows = uiModelList(),
                popularShows = uiModelList(),
                upcomingShows = uiModelList(),
                trendingToday = uiModelList(),
                nextEpisodes = nextEpisodeUiModelList(),
                startWatchingTitle = fakeLocalizer.getString(StringResourceKey.LabelStartWatching),
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
            setNextEpisodes(createNextEpisodesList())

            awaitItem() shouldBe DiscoverViewState(
                isInitial = false,
                featuredShows = uiModelList(),
                topRatedShows = uiModelList(),
                popularShows = uiModelList(),
                upcomingShows = uiModelList(),
                trendingToday = uiModelList(),
                nextEpisodes = nextEpisodeUiModelList(),
                startWatchingTitle = fakeLocalizer.getString(StringResourceKey.LabelStartWatching),
            )
        }
    }

    @Test
    fun `should return DataLoaded with refreshed data when refresh is clicked`() = runTest {
        presenter.state.test {
            setList(createDiscoverShowList())
            setNextEpisodes(createNextEpisodesList())

            val expectedList = uiModelList()
            val expectedResult = DiscoverViewState(
                isInitial = false,
                featuredShows = expectedList,
                topRatedShows = expectedList,
                popularShows = expectedList,
                upcomingShows = expectedList,
                trendingToday = expectedList,
                nextEpisodes = nextEpisodeUiModelList(),
                startWatchingTitle = fakeLocalizer.getString(StringResourceKey.LabelStartWatching),
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
                isInitial = false,
                featuredShows = expectedUpdatedList,
                topRatedShows = expectedUpdatedList,
                popularShows = expectedUpdatedList,
                upcomingShows = expectedUpdatedList,
                trendingToday = expectedUpdatedList,
                nextEpisodes = nextEpisodeUiModelList(),
                startWatchingTitle = fakeLocalizer.getString(StringResourceKey.LabelStartWatching),
            )
        }
    }

    @Test
    fun `should return DataLoaded when error occurs and refresh is clicked`() = runTest {
        setList(createDiscoverShowList())
        setNextEpisodes(createNextEpisodesList())

        presenter.state.test {
            awaitItem() shouldBe DiscoverViewState.Empty

            val expectedList = uiModelList()
            val expectedResult = DiscoverViewState(
                isInitial = false,
                featuredShows = expectedList,
                topRatedShows = expectedList,
                popularShows = expectedList,
                upcomingShows = expectedList,
                trendingToday = expectedList,
                nextEpisodes = nextEpisodeUiModelList(),
                startWatchingTitle = fakeLocalizer.getString(StringResourceKey.LabelStartWatching),
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
                isInitial = false,
                featuredShows = expectedUpdatedList,
                topRatedShows = expectedUpdatedList,
                popularShows = expectedUpdatedList,
                upcomingShows = expectedUpdatedList,
                trendingToday = expectedUpdatedList,
                nextEpisodes = nextEpisodeUiModelList(),
                startWatchingTitle = fakeLocalizer.getString(StringResourceKey.LabelStartWatching),
                featuredRefreshing = false,
                message = null,
            )

            awaitItem() shouldBe expectedUpdatedResult
        }
    }

    @Test
    fun `should navigate to season given next episode is clicked`() = runTest {
        val testNavigator = TestNavigator()

        val testPresenter = DiscoverShowsPresenter(
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            navigator = testNavigator,
            discoverShowsInteractor = DiscoverShowsInteractor(
                featuredShowsRepository = featuredShowsRepository,
                topRatedShowsRepository = topRatedShowsRepository,
                popularShowsRepository = popularShowsRepository,
                trendingShowsRepository = trendingShowsRepository,
                upcomingShowsRepository = upcomingShowsRepository,
                genreRepository = genreRepository,
            ),
            followShowInteractor = FollowShowInteractor(
                followedShowsRepository = followedShowsRepository,
                libraryRepository = FakeLibraryRepository(),
                syncShowMetadataInteractor = SyncShowMetadataInteractor(
                    showDetailsRepository = showDetailsRepository,
                    seasonDetailsRepository = seasonDetailsRepository,
                    watchProviderRepository = watchProviderRepository,
                    dispatchers = coroutineDispatcher,
                ),
                appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
            ),
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
            markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            unfollowShowInteractor = UnfollowShowInteractor(
                followedShowsRepository = followedShowsRepository,
                libraryRepository = FakeLibraryRepository(),
                appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
            ),
            observeStartWatchingInteractor = observeStartWatchingInteractor,
            observeUpNextInteractor = observeUpNextInteractor,
            traktAuthRepository = traktAuthRepository,
            localizer = fakeLocalizer,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

        testNavigator.test {
            testPresenter.dispatch(NextEpisodeClicked(showTraktId = 123L, seasonId = 10L, seasonNumber = 2L))

            awaitNavigateTo(
                SeasonDetailsRoute(
                    param = SeasonDetailsUiParam(
                        showTraktId = 123L,
                        seasonId = 10L,
                        seasonNumber = 2L,
                    ),
                ),
            )
        }
    }

    @Test
    fun `should map start watching shows into state`() = runTest {
        presenter.state.test {
            startWatchingRepository.setStartWatchingShows(startWatchingShowList)

            var state = awaitItem()
            while (state.startWatchingShows.isEmpty()) {
                state = awaitItem()
            }
            state.startWatchingShows shouldBe expectedStartWatchingDiscoverShows
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should switch to my shows when start watching more is clicked`() = runTest {
        val testNavigator = TestNavigator()
        val testPresenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            testPresenter.dispatch(StartWatchingMoreClicked)

            awaitSwitchBackStack(MyShowsRoot)
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

    private fun setNextEpisodes(episodes: List<NextEpisodeWithShow>) {
        upNextRepository.setNextEpisodesForWatchlist(episodes)
    }

    private fun createNextEpisodesList(size: Int = LIST_SIZE) = List(size) { index ->
        NextEpisodeWithShow(
            showTraktId = 84958L + index,
            showTmdbId = 84958L + index,
            showName = "Test Show $index",
            showPoster = "/test-poster-$index.jpg",
            showStatus = "Ended",
            showYear = "2024",
            episodeId = 1000L + index,
            episodeName = "Test Episode $index",
            seasonId = 2000L + index,
            seasonNumber = 1L,
            episodeNumber = index.toLong() + 1,
            runtime = 45L,
            stillPath = "/test-still-$index.jpg",
            overview = "Test episode overview $index",
            seasonCount = 2,
            episodeCount = 12,
            watchedCount = 0,
            totalCount = 10,
        )
    }.toImmutableList()

    private fun nextEpisodeUiModelList(size: Int = LIST_SIZE) =
        createNextEpisodesList(size).map { episode ->
            NextEpisodeUiModel(
                showTraktId = episode.showTraktId,
                showName = episode.showName!!,
                imageUrl = episode.stillPath ?: episode.showPoster,
                episodeId = episode.episodeId!!,
                episodeTitle = episode.episodeName ?: "",
                episodeNumberFormatted = "S${episode.seasonNumber}E${episode.episodeNumber}",
                seasonId = episode.seasonId!!,
                seasonNumber = episode.seasonNumber!!,
                episodeNumber = episode.episodeNumber!!,
                runtime = episode.runtime?.let { "$it min" },
                overview = episode.overview ?: "",
                isNew = false,
            )
        }.toImmutableList()

    private fun createDiscoverShowList(size: Int = LIST_SIZE) = List(size) {
        ShowEntity(
            traktId = 84958,
            tmdbId = 84958,
            title = "Loki",
            posterPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            inLibrary = false,
        )
    }.toImmutableList()

    private fun uiModelList(size: Int = LIST_SIZE) = createDiscoverShowList(size).map {
        DiscoverShow(
            tmdbId = it.tmdbId,
            traktId = it.traktId,
            title = it.title,
            posterImageUrl = it.posterPath,
            inLibrary = it.inLibrary,
            overView = it.overview,
        )
    }.toImmutableList()

    private val startWatchingShowList = listOf(
        StartWatchingShow(traktId = 1, tmdbId = 11, title = "Breaking Bad", posterPath = "/1.jpg", year = "2008", inLibrary = true),
    )

    private val expectedStartWatchingDiscoverShows = listOf(
        DiscoverShow(traktId = 1, tmdbId = 11, title = "Breaking Bad", posterImageUrl = "/1.jpg", inLibrary = true),
    ).toImmutableList()

    companion object {
        const val LIST_SIZE = 5
    }

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
        navigator: Navigator = NoOpNavigator(),
    ): DiscoverShowsPresenter = DiscoverShowsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        navigator = navigator,
        discoverShowsInteractor = DiscoverShowsInteractor(
            featuredShowsRepository = featuredShowsRepository,
            topRatedShowsRepository = topRatedShowsRepository,
            popularShowsRepository = popularShowsRepository,
            trendingShowsRepository = trendingShowsRepository,
            upcomingShowsRepository = upcomingShowsRepository,
            genreRepository = genreRepository,
        ),
        followShowInteractor = FollowShowInteractor(
            followedShowsRepository = followedShowsRepository,
            libraryRepository = FakeLibraryRepository(),
            syncShowMetadataInteractor = SyncShowMetadataInteractor(
                showDetailsRepository = showDetailsRepository,
                seasonDetailsRepository = seasonDetailsRepository,
                watchProviderRepository = watchProviderRepository,
                dispatchers = coroutineDispatcher,
            ),
            appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
        ),
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
        markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
            episodeRepository = episodeRepository,
        ),
        unfollowShowInteractor = UnfollowShowInteractor(
            followedShowsRepository = followedShowsRepository,
            libraryRepository = FakeLibraryRepository(),
            appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
        ),
        observeStartWatchingInteractor = observeStartWatchingInteractor,
        observeUpNextInteractor = observeUpNextInteractor,
        traktAuthRepository = traktAuthRepository,
        localizer = fakeLocalizer,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        logger = FakeLogger(),
    ).also { lifecycle.resume() }
}
