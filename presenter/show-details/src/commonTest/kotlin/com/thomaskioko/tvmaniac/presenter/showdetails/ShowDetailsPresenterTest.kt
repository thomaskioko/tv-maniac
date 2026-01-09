package com.thomaskioko.tvmaniac.presenter.showdetails

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.testing.FakeRecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.db.SelectByShowId
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveContinueTrackingInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveShowWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.recommendedshows.RecommendedShowsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.PrefetchFirstSeasonInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.episodes.testing.MarkEpisodeWatchedCall
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
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
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val watchProvidersRepository = FakeWatchProviderRepository()
    private val castRepository = FakeCastRepository()
    private val recommendedShowsRepository = FakeRecommendedShowsRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val fakeFormatterUtil = FakeFormatterUtil()
    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
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

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            val initialState = awaitItem()
            initialState.isRefreshing shouldBe true
            initialState.showDetails shouldBe ShowDetailsModel.Empty

            val emission = awaitUntil { it.showDetails.tmdbId != 0L }
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

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            val initialState = awaitItem()
            initialState.isRefreshing shouldBe true
            initialState.showDetails shouldBe ShowDetailsModel.Empty

            val emission = awaitUntil { it.showDetails.tmdbId != 0L }
            emission.showDetails shouldBe showDetailsContent.showDetails
            emission.isRefreshing shouldBe false
            emission.message shouldBe null

            seasonsRepository.setSeasonsResult(seasons)
            watchProvidersRepository.setWatchProvidersResult(watchProviderList)
            similarShowsRepository.setSimilarShowsResult(similarShowList)
            recommendedShowsRepository.setObserveRecommendedShows(recommendedShowList)
            trailerRepository.setTrailerResult(trailers)

            presenter.dispatch(ReloadShowDetails)

            val updatedState = awaitUntil { it.showDetails.seasonsList.isNotEmpty() }.showDetails
            updatedState.seasonsList.size shouldBe 1
            updatedState.similarShows.size shouldBe 1
            updatedState.providers.size shouldBe 1
        }
    }

    @Test
    fun `should update infoState to Loaded with correct data when ReloadShowDetails and fetching season fails`() =
        runTest {
            buildMockData()

            val presenter = buildShowDetailsPresenter()

            presenter.state.test {
                val initialState = awaitItem()
                initialState.isRefreshing shouldBe true
                initialState.showDetails shouldBe ShowDetailsModel.Empty

                val emission = awaitUntil { it.showDetails.tmdbId != 0L }
                emission.showDetails shouldBe showDetailsContent.showDetails
                emission.isRefreshing shouldBe false
                emission.message shouldBe null

                watchProvidersRepository.setWatchProvidersResult(watchProviderList)
                similarShowsRepository.setSimilarShowsResult(similarShowList)
                recommendedShowsRepository.setObserveRecommendedShows(recommendedShowList)
                trailerRepository.setTrailerResult(trailers)

                presenter.dispatch(ReloadShowDetails)

                val updatedState = awaitUntil { it.showDetails.similarShows.isNotEmpty() }.showDetails
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

    @Test
    fun `should display continue tracking episodes when available`() = runTest {
        buildMockData()
        episodeRepository.setContinueTrackingResult(testContinueTrackingResult)

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val emission = awaitUntil { it.continueTrackingEpisodes.isNotEmpty() }
            emission.continueTrackingEpisodes.size shouldBe 3
            emission.continueTrackingScrollIndex shouldBe 0
        }
    }

    @Test
    fun `should display watch progress when show is in library`() = runTest {
        buildMockData()
        episodeRepository.setShowWatchProgress(testShowWatchProgress)

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val emission = awaitUntil { it.showDetails.watchProgress > 0f }
            emission.showDetails.watchProgress shouldBe 0.5f
        }
    }

    @Test
    fun `should show completed status when all episodes watched`() = runTest {
        buildMockData()
        episodeRepository.setShowWatchProgress(
            testShowWatchProgress.copy(watchedCount = 10, totalCount = 10),
        )

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val emission = awaitUntil { it.showDetails.watchProgress == 1f }
            emission.showDetails.watchProgress shouldBe 1f
        }
    }

    @Test
    fun `should mark episode as watched when MarkEpisodeWatchedFromTracking is dispatched`() = runTest {
        buildMockData()

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()
            val _ = awaitUntil { it.showDetails.tmdbId != 0L }

            presenter.dispatch(
                MarkEpisodeWatched(
                    showId = 84958,
                    episodeId = 1001,
                    seasonNumber = 1,
                    episodeNumber = 1,
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            episodeRepository.lastMarkEpisodeWatchedCall shouldBe MarkEpisodeWatchedCall(
                showId = 84958,
                episodeId = 1001,
                seasonNumber = 1,
                episodeNumber = 1,
            )
        }
    }

    @Test
    fun `should update library when FollowShowClicked is dispatched`() = runTest {
        buildMockData()

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()
            val _ = awaitUntil { it.showDetails.tmdbId != 0L }

            presenter.dispatch(FollowShowClicked(isInLibrary = false))

            testDispatcher.scheduler.advanceUntilIdle()

            followedShowsRepository.addedShowIds shouldBe listOf(84958L)
        }
    }

    @Test
    fun `should update continue tracking list when episode is marked as watched`() = runTest {
        buildMockData()
        episodeRepository.setContinueTrackingResult(testContinueTrackingResult)

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val initialState = awaitUntil { it.continueTrackingEpisodes.isNotEmpty() }
            initialState.continueTrackingEpisodes.size shouldBe 3
            initialState.continueTrackingScrollIndex shouldBe 0

            val updatedTrackingResult = testContinueTrackingResult.copy(
                firstUnwatchedIndex = 1,
            )
            episodeRepository.setContinueTrackingResult(updatedTrackingResult)

            presenter.dispatch(
                MarkEpisodeWatched(
                    showId = 84958,
                    episodeId = 1001,
                    seasonNumber = 1,
                    episodeNumber = 1,
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = awaitUntil { it.continueTrackingScrollIndex == 1 }
            updatedState.continueTrackingScrollIndex shouldBe 1
        }
    }

    @Test
    fun `should clear continue tracking list when show is removed from library`() = runTest {
        buildMockData()
        episodeRepository.setContinueTrackingResult(testContinueTrackingResult)

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val initialState = awaitUntil { it.continueTrackingEpisodes.isNotEmpty() }
            initialState.continueTrackingEpisodes.size shouldBe 3

            episodeRepository.setContinueTrackingResult(null)

            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = awaitUntil { it.continueTrackingEpisodes.isEmpty() }
            updatedState.continueTrackingEpisodes.size shouldBe 0
        }
    }

    @Test
    fun `should display season progress when seasons have watched episodes`() = runTest {
        buildMockData(seasonResult = testSeasonsWithProgress)
        episodeRepository.setAllSeasonsWatchProgress(testSeasonWatchProgress)

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val emission = awaitUntil { it.showDetails.seasonsList.isNotEmpty() }
            val seasonsList = emission.showDetails.seasonsList

            seasonsList.size shouldBe 2

            val season1 = seasonsList.first { it.seasonNumber == 1L }
            season1.watchedCount shouldBe 8
            season1.totalCount shouldBe 10
            season1.progressPercentage shouldBe 0.8f
            season1.isSeasonWatched shouldBe false

            val season2 = seasonsList.first { it.seasonNumber == 2L }
            season2.watchedCount shouldBe 3
            season2.totalCount shouldBe 12
            season2.progressPercentage shouldBe 0.25f
            season2.isSeasonWatched shouldBe false
        }
    }

    @Test
    fun `should mark season as watched when all episodes are watched`() = runTest {
        buildMockData(seasonResult = testSeasonsWithProgress)
        episodeRepository.setAllSeasonsWatchProgress(testCompletedSeasonProgress)

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val emission = awaitUntil { it.showDetails.seasonsList.isNotEmpty() }
            val season1 = emission.showDetails.seasonsList.first { it.seasonNumber == 1L }

            season1.watchedCount shouldBe 10
            season1.totalCount shouldBe 10
            season1.progressPercentage shouldBe 1f
            season1.isSeasonWatched shouldBe true
        }
    }

    @Test
    fun `should display total episodes count in ShowDetailsModel`() = runTest {
        buildMockData()
        episodeRepository.setShowWatchProgress(
            testShowWatchProgress.copy(watchedCount = 12, totalCount = 38),
        )

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val emission = awaitUntil { it.showDetails.totalEpisodesCount > 0 }
            emission.showDetails.watchedEpisodesCount shouldBe 12
            emission.showDetails.totalEpisodesCount shouldBe 38
        }
    }

    @Test
    fun `should update season progress when episode is marked as watched`() = runTest {
        buildMockData(seasonResult = testSeasonsWithProgress)
        episodeRepository.setAllSeasonsWatchProgress(testPartialSeasonProgress)

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val initialState = awaitUntil { it.showDetails.seasonsList.isNotEmpty() }
            val season1Initial = initialState.showDetails.seasonsList.first { it.seasonNumber == 1L }
            season1Initial.watchedCount shouldBe 5
            season1Initial.progressPercentage shouldBe 0.5f

            episodeRepository.setAllSeasonsWatchProgress(
                listOf(
                    testPartialSeasonProgress.first().copy(watchedCount = 6),
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = awaitUntil {
                it.showDetails.seasonsList.firstOrNull { s -> s.seasonNumber == 1L }?.watchedCount == 6
            }
            val season1Updated = updatedState.showDetails.seasonsList.first { it.seasonNumber == 1L }
            season1Updated.watchedCount shouldBe 6
            season1Updated.progressPercentage shouldBe 0.6f
        }
    }

    @Test
    fun `should display zero progress when no episodes are watched`() = runTest {
        buildMockData(seasonResult = testSeasonsWithProgress)
        episodeRepository.setAllSeasonsWatchProgress(emptyList())

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()

            val emission = awaitUntil { it.showDetails.seasonsList.isNotEmpty() }
            val season1 = emission.showDetails.seasonsList.first { it.seasonNumber == 1L }

            season1.watchedCount shouldBe 0
            season1.totalCount shouldBe 0
            season1.progressPercentage shouldBe 0f
            season1.isSeasonWatched shouldBe false
        }
    }

    private suspend fun buildMockData(
        isYoutubeInstalled: Boolean = false,
        castList: List<ShowCast> = emptyList(),
        showDetailResult: TvshowDetails = tvShowDetails,
        seasonResult: List<ShowSeasons> = emptyList(),
        watchProviderResult: List<WatchProviders> = emptyList(),
        similarShowResult: List<SimilarShows> = emptyList(),
        recommendedShowResult: List<RecommendedShows> = emptyList(),
        trailersResult: List<SelectByShowId> = emptyList(),
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
        return DefaultShowDetailsPresenter(
            showId = 84958,
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            onBack = onBack,
            onNavigateToSeason = onNavigateToSeason,
            onNavigateToShow = onNavigateToShow,
            onNavigateToTrailer = onNavigateToTrailer,
            followedShowsRepository = followedShowsRepository,
            recommendedShowsInteractor = RecommendedShowsInteractor(
                recommendedShowsRepository = recommendedShowsRepository,
                dispatchers = coroutineDispatcher,
            ),
            showDetailsInteractor = ShowDetailsInteractor(
                showDetailsRepository = showDetailsRepository,
                dispatchers = coroutineDispatcher,
            ),
            prefetchFirstSeasonInteractor = PrefetchFirstSeasonInteractor(
                seasonsRepository = seasonsRepository,
                seasonDetailsRepository = seasonDetailsRepository,
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
                episodeRepository = episodeRepository,
                recommendedShowsRepository = recommendedShowsRepository,
                seasonsRepository = seasonsRepository,
                showDetailsRepository = showDetailsRepository,
                similarShowsRepository = similarShowsRepository,
                trailerRepository = trailerRepository,
                watchProviders = watchProvidersRepository,
                formatterUtil = fakeFormatterUtil,
                dispatchers = coroutineDispatcher,
            ),
            markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            observeShowWatchProgressInteractor = ObserveShowWatchProgressInteractor(
                episodeRepository = episodeRepository,
            ),
            observeContinueTrackingInteractor = ObserveContinueTrackingInteractor(
                episodeRepository = episodeRepository,
            ),
            showContentSyncInteractor = ShowContentSyncInteractor(
                showDetailsRepository = showDetailsRepository,
                seasonsRepository = seasonsRepository,
                seasonDetailsRepository = seasonDetailsRepository,
                watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
                dispatchers = coroutineDispatcher,
            ),
            logger = FakeLogger(),
        )
    }

    private suspend fun <T> ReceiveTurbine<T>.awaitUntil(
        maxAttempts: Int = 10,
        predicate: (T) -> Boolean,
    ): T {
        repeat(maxAttempts) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
        throw AssertionError("Condition not met after $maxAttempts attempts")
    }
}
