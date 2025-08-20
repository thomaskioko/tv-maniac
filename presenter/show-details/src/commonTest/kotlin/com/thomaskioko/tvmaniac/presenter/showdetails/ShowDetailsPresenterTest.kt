package com.thomaskioko.tvmaniac.presenter.showdetails

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.testing.FakeRecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.domain.recommendedshows.RecommendedShowsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ShowDetailsPresenterTest {

    private val seasonsRepository = FakeSeasonsRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()
    private val watchlistRepository = FakeWatchlistRepository()
    private val watchProvidersRepository = FakeWatchProviderRepository()
    private val castRepository = FakeCastRepository()
    private val recommendedShowsRepository = FakeRecommendedShowsRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val fakeFormatterUtil = FakeFormatterUtil()
    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var presenter: ShowDetailsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        presenter = buildShowDetailsPresenter()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return SeasonDetailsLoaded when all data is available`() = runTest {
        buildMockData(
            showDetailResult = tvShowDetails,
            watchProviderResult = watchProviderList,
            similarShowResult = similarShowList,
            recommendedShowResult = recommendedShowList,
            trailersResult = trailers,
        )

        presenter.state.test {
            awaitItem() shouldBe ShowDetailsContent.Empty

            val emission = awaitItem()
            emission.showDetails shouldBe showDetailsContent.showDetails.copy(
                recommendedShows = persistentListOf(
                    ShowModel(
                        tmdbId = 184958,
                        title = "Loki",
                        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                        isInLibrary = false,
                    ),
                ),
                providers = persistentListOf(
                    ProviderModel(
                        id = 184958,
                        logoUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                        name = "Netflix",
                    ),
                ),
                similarShows = persistentListOf(
                    ShowModel(
                        tmdbId = 184958,
                        title = "Loki",
                        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                        isInLibrary = false,
                    ),
                ),
                trailersList = persistentListOf(
                    TrailerModel(
                        showId = 84958,
                        key = "Fd43V",
                        name = "Some title",
                        youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
                    ),
                ),
            )
            emission.isRefreshing shouldBe false
            emission.message shouldBe null
        }
    }

    @Test
    fun `should update state to Loaded when ReloadShowDetails and new data is available`() = runTest {
        buildMockData()

        presenter.state.test {
            awaitItem() shouldBe ShowDetailsContent.Empty

            val emission = awaitItem()
            emission.showDetails shouldBe showDetailsContent.showDetails
            emission.isRefreshing shouldBe false
            emission.message shouldBe null

            seasonsRepository.setSeasonsResult(seasons)
            watchProvidersRepository.setWatchProvidersResult(watchProviderList)
            similarShowsRepository.setSimilarShowsResult(similarShowList)
            recommendedShowsRepository.setObserveRecommendedShows(recommendedShowList)
            trailerRepository.setTrailerResult(trailers)

            presenter.dispatch(ReloadShowDetails)

            val updatedState = awaitItem().showDetails
            updatedState.seasonsList.size shouldBe 1
            updatedState.similarShows.size shouldBe 1
            updatedState.providers.size shouldBe 1
        }
    }

    @Test
    fun `should update infoState to Loaded with correct data when ReloadShowDetails and fetching season fails`() =
        runTest {
            buildMockData()

            presenter.state.test {
                awaitItem() shouldBe ShowDetailsContent.Empty

                val emission = awaitItem()
                emission.showDetails shouldBe showDetailsContent.showDetails
                emission.isRefreshing shouldBe false
                emission.message shouldBe null

                watchProvidersRepository.setWatchProvidersResult(watchProviderList)
                similarShowsRepository.setSimilarShowsResult(similarShowList)
                recommendedShowsRepository.setObserveRecommendedShows(recommendedShowList)
                trailerRepository.setTrailerResult(trailers)

                presenter.dispatch(ReloadShowDetails)

                val updatedState = awaitItem().showDetails
                updatedState.seasonsList.size shouldBe 0
                updatedState.similarShows.size shouldBe 1
                updatedState.providers.size shouldBe 1
            }
        }

    @Test
    fun `should invoke navigateToSeason when SeasonClicked`() = runTest {
        var navigatedToSeason = false
        val presenter = buildShowDetailsPresenter(
            onNavigateToSeason = { navigatedToSeason = true },
        )

        presenter.dispatch(
            SeasonClicked(
                ShowSeasonDetailsParam(
                    showId = 2,
                    selectedSeasonIndex = 2,
                    seasonNumber = 0,
                    seasonId = 0,
                ),
            ),
        )

        navigatedToSeason shouldBe true
    }

    private suspend fun buildMockData(
        isYoutubeInstalled: Boolean = false,
        castList: List<ShowCast> = emptyList(),
        showDetailResult: TvshowDetails = tvShowDetails,
        seasonResult: List<ShowSeasons> = emptyList(),
        watchProviderResult: List<WatchProviders> = emptyList(),
        similarShowResult: List<SimilarShows> = emptyList(),
        recommendedShowResult: List<RecommendedShows> = emptyList(),
        trailersResult: List<Trailers> = emptyList(),
    ) {
        showDetailsRepository.setShowDetailsResult(showDetailResult)
        trailerRepository.setYoutubePlayerInstalled(isYoutubeInstalled)
        seasonsRepository.setSeasonsResult(seasonResult)
        castRepository.setShowCast(castList)
        watchProvidersRepository.setWatchProvidersResult(watchProviderResult)
        similarShowsRepository.setSimilarShowsResult(similarShowResult)
        recommendedShowsRepository.setObserveRecommendedShows(recommendedShowResult)
        trailerRepository.setTrailerResult(trailersResult)
    }

    private fun buildShowDetailsPresenter(
        onBack: () -> Unit = {},
        onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit = {},
        onNavigateToTrailer: (id: Long) -> Unit = {},
        onNavigateToShow: (id: Long) -> Unit = {},
    ): ShowDetailsPresenter {
        return ShowDetailsPresenter(
            showId = 84958,
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            onBack = onBack,
            onNavigateToSeason = onNavigateToSeason,
            onNavigateToShow = onNavigateToShow,
            onNavigateToTrailer = onNavigateToTrailer,
            watchlistRepository = watchlistRepository,
            recommendedShowsInteractor = RecommendedShowsInteractor(
                recommendedShowsRepository = recommendedShowsRepository,
                dispatchers = coroutineDispatcher,
            ),
            showDetailsInteractor = ShowDetailsInteractor(
                showDetailsRepository = showDetailsRepository,
                dispatchers = coroutineDispatcher,
            ),
            similarShowsInteractor = SimilarShowsInteractor(
                similarShowsRepository = similarShowsRepository,
                dispatchers = coroutineDispatcher,
            ),
            watchProvidersInteractor = WatchProvidersInteractor(
                repository = watchProvidersRepository,
                dispatchers = coroutineDispatcher,
            ),
            observableShowDetailsInteractor = ObservableShowDetailsInteractor(
                castRepository = castRepository,
                recommendedShowsRepository = recommendedShowsRepository,
                seasonsRepository = seasonsRepository,
                showDetailsRepository = showDetailsRepository,
                similarShowsRepository = similarShowsRepository,
                trailerRepository = trailerRepository,
                watchProviders = watchProvidersRepository,
                formatterUtil = fakeFormatterUtil,
                dispatchers = coroutineDispatcher,
            ),
            logger = FakeLogger(),
        )
    }
}
