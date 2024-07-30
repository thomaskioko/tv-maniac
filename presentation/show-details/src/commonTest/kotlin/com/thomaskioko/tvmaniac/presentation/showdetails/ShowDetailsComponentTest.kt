package com.thomaskioko.tvmaniac.presentation.showdetails

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.db.RecommendedShows
import com.thomaskioko.tvmaniac.core.db.ShowCast
import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.core.networkutil.model.ServerError
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.testing.FakeRecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.watchlist.testing.FakeLibraryRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class ShowDetailsComponentTest {

  private val seasonsRepository = FakeSeasonsRepository()
  private val trailerRepository = FakeTrailerRepository()
  private val similarShowsRepository = FakeSimilarShowsRepository()
  private val libraryRepository = FakeLibraryRepository()
  private val watchProviders = FakeWatchProviderRepository()
  private val castRepository = FakeCastRepository()
  private val recommendedShowsRepository = FakeRecommendedShowsRepository()
  private val showDetailsRepository = FakeShowDetailsRepository()
  private val testDispatcher = StandardTestDispatcher()

  private lateinit var component: ShowDetailsComponent

  @BeforeTest
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    component = buildShowDetailsPresenter()
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `should return SeasonDetailsLoaded when all data is available`() = runTest {
    buildMockData(
      showDetailResult = Either.Right(tvShowDetails),
      seasonResult = Either.Right(emptyList()),
      castList = emptyList(),
      watchProviderResult = Either.Right(watchProviderList),
      similarShowResult = Either.Right(similarShowList),
      recommendedShowResult = Either.Right(recommendedShowList),
      trailersResult = Either.Right(trailers),
    )

    component.state.test {
      awaitItem() shouldBe
        ShowDetailsContent(
          showDetails = null,
          isUpdating = true,
        )

      val emission = awaitItem()
      emission.showDetails shouldBe showDetailsContent.showDetails
      emission.isUpdating shouldBe false
      emission.errorMessage shouldBe null

      emission.showInfo.shouldBeInstanceOf<ShowInfoState.Loaded>()
    }
  }

  @Test
  fun `should update state with error when show details fetch fails`() = runTest {
    val errorMessage = "Failed to fetch show details"
    buildMockData(showDetailResult = Either.Left(ServerError(errorMessage)))

    component.state.test {
      awaitItem() shouldBe
        ShowDetailsContent(
          showDetails = null,
          isUpdating = true,
        )

      awaitItem() shouldBe
        ShowDetailsContent(
          showDetails = null,
          isUpdating = false,
          errorMessage = errorMessage,
          showInfo = ShowInfoState.Error
        )
    }
  }

  @Test
  fun `should update state to Loaded when ReloadShowDetails and new data is available`() = runTest {
    buildMockData()

    component.state.test {
      awaitItem() shouldBe
        ShowDetailsContent(
          showDetails = null,
          isUpdating = true,
        )

      val emission = awaitItem()
      emission.showDetails shouldBe showDetailsContent.showDetails
      emission.isUpdating shouldBe false
      emission.errorMessage shouldBe null
      emission.showInfo shouldBe ShowInfoState.Empty

      seasonsRepository.setSeasonsResult(Either.Right(seasons))
      watchProviders.setWatchProvidersResult(Either.Right(watchProviderList))
      similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowList))
      recommendedShowsRepository.setObserveRecommendedShows(Either.Right(recommendedShowList))
      trailerRepository.setTrailerResult(Either.Right(trailers))

      component.dispatch(ReloadShowDetails)

      awaitItem().showInfo.shouldBeInstanceOf<ShowInfoState.Loading>()

      val updatedState = awaitItem().showInfo
      updatedState.shouldBeInstanceOf<ShowInfoState.Loaded>()
      updatedState.seasonsList.size shouldBe 1
      updatedState.similarShows.size shouldBe 1
      updatedState.providers.size shouldBe 1
    }
  }

  @Test
  fun `should update infoState to Loaded with correct data when ReloadShowDetails and fetching season fails`() =
    runTest {
      val errorMessage = "Failed to fetch show details"
      buildMockData()

      component.state.test {
        awaitItem() shouldBe
          ShowDetailsContent(
            showDetails = null,
            isUpdating = true,
          )

        val emission = awaitItem()
        emission.showDetails shouldBe showDetailsContent.showDetails
        emission.isUpdating shouldBe false
        emission.errorMessage shouldBe null
        emission.showInfo shouldBe ShowInfoState.Empty

        seasonsRepository.setSeasonsResult(Either.Left(ServerError(errorMessage)))
        watchProviders.setWatchProvidersResult(Either.Right(watchProviderList))
        similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowList))
        recommendedShowsRepository.setObserveRecommendedShows(Either.Right(recommendedShowList))
        trailerRepository.setTrailerResult(Either.Right(trailers))

        component.dispatch(ReloadShowDetails)

        awaitItem().showInfo.shouldBeInstanceOf<ShowInfoState.Loading>()

        val updatedState = awaitItem().showInfo
        updatedState.shouldBeInstanceOf<ShowInfoState.Loaded>()
        updatedState.seasonsList.size shouldBe 0
        updatedState.similarShows.size shouldBe 1
        updatedState.providers.size shouldBe 1
      }
    }

  @Test
  fun `should clear error message when DismissErrorSnackbar`() = runTest {
    val errorMessage = "Failed to fetch show details"

    buildMockData(
      showDetailResult = Either.Left(ServerError(errorMessage)),
    )

    component.state.test {
      awaitItem() shouldBe
        ShowDetailsContent(
          showDetails = null,
          isUpdating = true,
        )

      awaitItem() shouldBe
        ShowDetailsContent(
          showDetails = null,
          isUpdating = false,
          errorMessage = "Failed to fetch show details",
          showInfo = ShowInfoState.Error
        )

      component.dispatch(DismissErrorSnackbar)

      val updatedState = awaitItem()
      updatedState.errorMessage shouldBe null
    }
  }

  @Test
  fun `should invoke navigateToSeason when SeasonClicked`() = runTest {
    var navigatedToSeason = false
    val presenter =
      buildShowDetailsPresenter(
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
    isYoutubeInstalled: Boolean = true,
    castList: List<ShowCast> = emptyList(),
    showDetailResult: Either<Failure, TvshowDetails> = Either.Right(tvShowDetails),
    seasonResult: Either<Failure, List<ShowSeasons>> = Either.Right(emptyList()),
    watchProviderResult: Either<Failure, List<WatchProviders>> = Either.Right(emptyList()),
    similarShowResult: Either<Failure, List<SimilarShows>> = Either.Right(emptyList()),
    recommendedShowResult: Either<Failure, List<RecommendedShows>> = Either.Right(emptyList()),
    trailersResult: Either<Failure, List<Trailers>> = Either.Right(emptyList()),
  ) {
    showDetailsRepository.setShowDetailsResult(showDetailResult)
    trailerRepository.setYoutubePlayerInstalled(isYoutubeInstalled)
    seasonsRepository.setSeasonsResult(seasonResult)
    castRepository.setShowCast(castList)
    watchProviders.setWatchProvidersResult(watchProviderResult)
    similarShowsRepository.setSimilarShowsResult(similarShowResult)
    recommendedShowsRepository.setObserveRecommendedShows(recommendedShowResult)
    trailerRepository.setTrailerResult(trailersResult)
  }

  private fun buildShowDetailsPresenter(
    onBack: () -> Unit = {},
    onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit = {},
    onNavigateToTrailer: (id: Long) -> Unit = {},
    onNavigateToShow: (id: Long) -> Unit = {},
  ): ShowDetailsComponent {
    return ShowDetailsComponent(
      showId = 84958,
      componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
      onBack = onBack,
      onNavigateToSeason = onNavigateToSeason,
      onNavigateToShow = onNavigateToShow,
      onNavigateToTrailer = onNavigateToTrailer,
      trailerRepository = trailerRepository,
      seasonsRepository = seasonsRepository,
      similarShowsRepository = similarShowsRepository,
      libraryRepository = libraryRepository,
      watchProviders = watchProviders,
      recommendedShowsRepository = recommendedShowsRepository,
      castRepository = castRepository,
      showDetailsRepository = showDetailsRepository,
    )
  }
}
