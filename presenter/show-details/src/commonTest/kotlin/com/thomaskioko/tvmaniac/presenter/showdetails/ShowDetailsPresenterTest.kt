package com.thomaskioko.tvmaniac.presenter.showdetails

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.SelectByShowTraktId
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveShowWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncTraktCalendarInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.episodes.testing.MarkEpisodeUnwatchedCall
import com.thomaskioko.tvmaniac.episodes.testing.MarkEpisodeWatchedCall
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.testing.util.IgnoreIos
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import com.thomaskioko.tvmaniac.seasondetails.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

@IgnoreIos
class ShowDetailsPresenterTest {

    private val seasonsRepository = FakeSeasonsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val watchProvidersRepository = FakeWatchProviderRepository()
    private val castRepository = FakeCastRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()
    private val fakeFormatterUtil = FakeFormatterUtil()
    private val fakeNotificationManager = FakeNotificationManager()
    private val fakeDatastoreRepository = FakeDatastoreRepository()
    private val fakeLogger = FakeLogger()
    private val fakeDateTimeProvider = FakeDateTimeProvider()
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
        fakeNotificationManager.reset()
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
            trailersResult = trailers,
        )

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = presenter.state.value
        state.showDetails shouldBe showDetailsContent.showDetails.copy(
            providers = persistentListOf(
                ProviderModel(
                    id = 184958,
                    logoUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    name = "Netflix",
                ),
            ),
            similarShows = persistentListOf(
                ShowModel(
                    traktId = 18495,
                    title = "Loki",
                    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    isInLibrary = false,
                ),
            ),
            trailersList = persistentListOf(
                TrailerModel(
                    showTmdbId = 84958,
                    key = "Fd43V",
                    name = "Some title",
                    youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
                ),
            ),
        )
        state.isRefreshing shouldBe false
        state.message shouldBe null
    }

    @Test
    fun `should update state to Loaded when ReloadShowDetails and new data is available`() =
        runTest {
            buildMockData()

            val presenter = buildShowDetailsPresenter()
            testDispatcher.scheduler.advanceUntilIdle()

            val initialState = presenter.state.value
            initialState.showDetails shouldBe showDetailsContent.showDetails
            initialState.isRefreshing shouldBe false
            initialState.message shouldBe null

            seasonsRepository.setSeasonsResult(seasons)
            watchProvidersRepository.setWatchProvidersResult(watchProviderList)
            similarShowsRepository.setSimilarShowsResult(similarShowList)
            trailerRepository.setTrailerResult(trailers)

            presenter.dispatch(ReloadShowDetails)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = presenter.state.value.showDetails
            updatedState.seasonsList.size shouldBe 1
            updatedState.similarShows.size shouldBe 1
            updatedState.providers.size shouldBe 1
        }

    @Test
    fun `should update infoState to Loaded with correct data when ReloadShowDetails and fetching season fails`() =
        runTest {
            buildMockData()

            val presenter = buildShowDetailsPresenter()
            testDispatcher.scheduler.advanceUntilIdle()

            val initialState = presenter.state.value
            initialState.showDetails shouldBe showDetailsContent.showDetails
            initialState.isRefreshing shouldBe false
            initialState.message shouldBe null

            watchProvidersRepository.setWatchProvidersResult(watchProviderList)
            similarShowsRepository.setSimilarShowsResult(similarShowList)
            trailerRepository.setTrailerResult(trailers)

            presenter.dispatch(ReloadShowDetails)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = presenter.state.value.showDetails
            updatedState.seasonsList.size shouldBe 0
            updatedState.similarShows.size shouldBe 1
            updatedState.providers.size shouldBe 1
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
                    showTraktId = 2,
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
        seasonDetailsRepository.setContinueTrackingResult(testContinueTrackingResult)

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = presenter.state.value
        state.continueTrackingEpisodes.size shouldBe 3
        state.continueTrackingScrollIndex shouldBe 0
    }

    @Test
    fun `should display watch progress when show is in library`() = runTest {
        buildMockData()
        episodeRepository.setShowWatchProgress(testShowWatchProgress)

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = presenter.state.value
        state.showDetails.watchProgress shouldBe 0.5f
    }

    @Test
    fun `should show completed status when all episodes watched`() = runTest {
        buildMockData()
        episodeRepository.setShowWatchProgress(
            testShowWatchProgress.copy(watchedCount = 10, totalCount = 10),
        )

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = presenter.state.value
        state.showDetails.watchProgress shouldBe 1f
    }

    @Test
    fun `should mark episode as watched when MarkEpisodeWatchedFromTracking is dispatched`() =
        runTest {
            buildMockData()

            val presenter = buildShowDetailsPresenter()
            testDispatcher.scheduler.advanceUntilIdle()

            presenter.dispatch(
                MarkEpisodeWatched(
                    showTraktId = 84958,
                    episodeId = 1001,
                    seasonNumber = 1,
                    episodeNumber = 1,
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            episodeRepository.lastMarkEpisodeWatchedCall shouldBe MarkEpisodeWatchedCall(
                showTraktId = 84958,
                episodeId = 1001,
                seasonNumber = 1,
                episodeNumber = 1,
            )
        }

    @Test
    fun `should mark episode as unwatched when MarkEpisodeUnwatched is dispatched`() =
        runTest {
            buildMockData()

            val presenter = buildShowDetailsPresenter()
            testDispatcher.scheduler.advanceUntilIdle()

            presenter.dispatch(
                MarkEpisodeUnwatched(
                    showTraktId = 84958,
                    episodeId = 1001,
                ),
            )

            testDispatcher.scheduler.advanceUntilIdle()

            episodeRepository.lastMarkEpisodeUnwatchedCall shouldBe MarkEpisodeUnwatchedCall(
                showTraktId = 84958,
                episodeId = 1001,
            )
        }

    @Test
    fun `should update library when FollowShowClicked is dispatched`() = runTest {
        buildMockData()

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(FollowShowClicked(isInLibrary = false))

        testDispatcher.scheduler.advanceUntilIdle()

        followedShowsRepository.addedShowIds shouldBe listOf(84958L)
    }

    @Test
    fun `should invoke onShowFollowed callback when following a show`() = runTest {
        buildMockData()
        var onShowFollowedCalled = false

        val presenter = buildShowDetailsPresenter(
            onShowFollowed = { onShowFollowedCalled = true },
        )
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(FollowShowClicked(isInLibrary = false))
        testDispatcher.scheduler.advanceUntilIdle()

        onShowFollowedCalled shouldBe true
    }

    @Test
    fun `should not invoke onShowFollowed callback when unfollowing a show`() = runTest {
        buildMockData()
        var onShowFollowedCalled = false

        val presenter = buildShowDetailsPresenter(
            onShowFollowed = { onShowFollowedCalled = true },
        )
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(FollowShowClicked(isInLibrary = true))
        testDispatcher.scheduler.advanceUntilIdle()

        onShowFollowedCalled shouldBe false
    }

    @Test
    fun `should schedule episode notifications when following a show with notifications enabled`() = runTest {
        buildMockData()
        fakeDatastoreRepository.setEpisodeNotificationsEnabled(true)

        fakeDateTimeProvider.setCurrentTimeMillis(LocalDate(2025, 1, 1).toEpochMillis())

        episodeRepository.setUpcomingEpisodes(
            listOf(
                UpcomingEpisode(
                    episodeId = 101,
                    seasonId = 1,
                    showId = 84958,
                    episodeNumber = 1,
                    seasonNumber = 1,
                    title = "Episode 1",
                    overview = "Overview",
                    runtime = 45,
                    imageUrl = "/still.jpg",
                    firstAired = LocalDate(2025, 1, 20).toEpochMillis(),
                    showName = "Loki",
                    showPoster = "/poster.jpg",
                ),
            ),
        )

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()
            awaitItem()

            presenter.dispatch(FollowShowClicked(isInLibrary = false))

            testDispatcher.scheduler.advanceUntilIdle()

            fakeNotificationManager.getPendingNotifications().size shouldBe 1
        }
    }

    @Test
    fun `should not schedule notifications when following a show with notifications disabled`() = runTest {
        buildMockData()
        fakeDatastoreRepository.setEpisodeNotificationsEnabled(false)

        val presenter = buildShowDetailsPresenter()

        presenter.state.test {
            awaitItem()
            awaitItem()

            presenter.dispatch(FollowShowClicked(isInLibrary = false))

            testDispatcher.scheduler.advanceUntilIdle()

            fakeNotificationManager.getScheduledNotifications() shouldBe emptyMap()
        }
    }

    @Test
    fun `should cancel notifications for show when unfollowing`() = runTest {
        buildMockData()
        fakeNotificationManager.addPendingNotification(
            EpisodeNotification(
                id = 1,
                showId = 84958,
                seasonId = 1,
                showName = "Loki",
                episodeTitle = "Episode 1",
                seasonNumber = 1,
                episodeNumber = 1,
                imageUrl = null,
                scheduledTime = 1000L,
            ),
        )
        fakeNotificationManager.addPendingNotification(
            EpisodeNotification(
                id = 2,
                showId = 99999,
                seasonId = 2,
                showName = "Other Show",
                episodeTitle = "Episode 1",
                seasonNumber = 1,
                episodeNumber = 1,
                imageUrl = null,
                scheduledTime = 2000L,
            ),
        )

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(FollowShowClicked(isInLibrary = true))
        testDispatcher.scheduler.advanceUntilIdle()

        val pendingNotifications = fakeNotificationManager.getPendingNotifications()
        pendingNotifications.filter { it.showId == 84958L } shouldBe emptyList()
        pendingNotifications.filter { it.showId == 99999L }.size shouldBe 1
    }

    @Test
    fun `should update continue tracking list when episode is marked as watched`() = runTest {
        buildMockData()
        seasonDetailsRepository.setContinueTrackingResult(testContinueTrackingResult)

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = presenter.state.value
        initialState.continueTrackingEpisodes.size shouldBe 3
        initialState.continueTrackingScrollIndex shouldBe 0

        val updatedTrackingResult = ContinueTrackingResult(
            episodes = listOf(
                testEpisodeDetails.copy(isWatched = true),
                testEpisodeDetails.copy(id = 1002L, episodeNumber = 2L, name = "Episode 2"),
                testEpisodeDetails.copy(id = 1003L, episodeNumber = 3L, name = "Episode 3"),
            ).toImmutableList(),
            currentSeasonNumber = 1L,
            currentSeasonId = 101L,
        )
        seasonDetailsRepository.setContinueTrackingResult(updatedTrackingResult)

        presenter.dispatch(
            MarkEpisodeWatched(
                showTraktId = 84958,
                episodeId = 1001,
                seasonNumber = 1,
                episodeNumber = 1,
            ),
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val updatedState = presenter.state.value
        updatedState.continueTrackingScrollIndex shouldBe 1
    }

    @Test
    fun `should clear continue tracking list when show is removed from library`() = runTest {
        buildMockData()
        seasonDetailsRepository.setContinueTrackingResult(testContinueTrackingResult)

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = presenter.state.value
        initialState.continueTrackingEpisodes.size shouldBe 3

        seasonDetailsRepository.setContinueTrackingResult(null)

        testDispatcher.scheduler.advanceUntilIdle()

        val updatedState = presenter.state.value
        updatedState.continueTrackingEpisodes.size shouldBe 0
    }

    @Test
    fun `should display season progress when seasons have watched episodes`() = runTest {
        buildMockData(seasonResult = testSeasonsWithProgress)
        episodeRepository.setAllSeasonsWatchProgress(testSeasonWatchProgress)

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = presenter.state.value
        val seasonsList = state.showDetails.seasonsList

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

    @Test
    fun `should mark season as watched when all episodes are watched`() = runTest {
        buildMockData(seasonResult = testSeasonsWithProgress)
        episodeRepository.setAllSeasonsWatchProgress(testCompletedSeasonProgress)

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = presenter.state.value
        val season1 = state.showDetails.seasonsList.first { it.seasonNumber == 1L }

        season1.watchedCount shouldBe 10
        season1.totalCount shouldBe 10
        season1.progressPercentage shouldBe 1f
        season1.isSeasonWatched shouldBe true
    }

    @Test
    fun `should display total episodes count in ShowDetailsModel`() = runTest {
        buildMockData()
        episodeRepository.setShowWatchProgress(
            testShowWatchProgress.copy(watchedCount = 12, totalCount = 38),
        )

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = presenter.state.value
        state.showDetails.watchedEpisodesCount shouldBe 12
        state.showDetails.totalEpisodesCount shouldBe 38
    }

    @Test
    fun `should update season progress when episode is marked as watched`() = runTest {
        buildMockData(seasonResult = testSeasonsWithProgress)
        episodeRepository.setAllSeasonsWatchProgress(testPartialSeasonProgress)

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = presenter.state.value
        val season1Initial = initialState.showDetails.seasonsList.first { it.seasonNumber == 1L }
        season1Initial.watchedCount shouldBe 5
        season1Initial.progressPercentage shouldBe 0.5f

        episodeRepository.setAllSeasonsWatchProgress(
            listOf(
                testPartialSeasonProgress.first().copy(watchedCount = 6),
            ),
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val updatedState = presenter.state.value
        val season1Updated = updatedState.showDetails.seasonsList.first { it.seasonNumber == 1L }
        season1Updated.watchedCount shouldBe 6
        season1Updated.progressPercentage shouldBe 0.6f
    }

    @Test
    fun `should display zero progress when no episodes are watched`() = runTest {
        buildMockData(seasonResult = testSeasonsWithProgress)
        episodeRepository.setAllSeasonsWatchProgress(emptyList())

        val presenter = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = presenter.state.value
        val season1 = state.showDetails.seasonsList.first { it.seasonNumber == 1L }

        season1.watchedCount shouldBe 0
        season1.totalCount shouldBe 0
        season1.progressPercentage shouldBe 0f
        season1.isSeasonWatched shouldBe false
    }

    @Test
    fun `should sync watched episodes given auth state changes to logged in`() = runTest {
        buildMockData(seasonResult = seasons)

        val _ = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        watchedEpisodeSyncRepository.reset()

        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        testDispatcher.scheduler.advanceUntilIdle()

        watchedEpisodeSyncRepository.getLastSyncedShowId() shouldBe 84958L
        watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe true
    }

    @Test
    fun `should sync watch progress on initial load given user is logged in`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        buildMockData(seasonResult = seasons)

        val _ = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        watchedEpisodeSyncRepository.getLastSyncedShowId() shouldBe 84958L
        watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe false
    }

    @Test
    fun `should not sync watch progress on initial load given user is logged out`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_OUT)
        buildMockData(seasonResult = seasons)

        val _ = buildShowDetailsPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        watchedEpisodeSyncRepository.getLastSyncedShowId() shouldBe null
    }

    private suspend fun buildMockData(
        isYoutubeInstalled: Boolean = false,
        castList: List<ShowCast> = emptyList(),
        showDetailResult: TvshowDetails = tvShowDetails,
        seasonResult: List<ShowSeasons> = emptyList(),
        watchProviderResult: List<WatchProviders> = emptyList(),
        similarShowResult: List<SimilarShows> = emptyList(),
        trailersResult: List<SelectByShowTraktId> = emptyList(),
    ) {
        showDetailsRepository.setShowDetailsResult(showDetailResult)
        trailerRepository.setYoutubePlayerInstalled(isYoutubeInstalled)
        seasonsRepository.setSeasonsResult(seasonResult)
        castRepository.setShowCast(castList)
        watchProvidersRepository.setWatchProvidersResult(watchProviderResult)
        similarShowsRepository.setSimilarShowsResult(similarShowResult)
        trailerRepository.setTrailerResult(trailersResult)
    }

    private fun buildShowDetailsPresenter(
        param: ShowDetailsParam = ShowDetailsParam(id = 84958),
        onBack: () -> Unit = {},
        onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit = {},
        onNavigateToTrailer: (id: Long) -> Unit = {},
        onNavigateToShow: (id: Long) -> Unit = {},
        onShowFollowed: () -> Unit = {},
    ): ShowDetailsPresenter {
        return DefaultShowDetailsPresenter(
            param = param,
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            onBack = onBack,
            onNavigateToSeason = onNavigateToSeason,
            onNavigateToShow = onNavigateToShow,
            onNavigateToTrailer = onNavigateToTrailer,
            onShowFollowed = onShowFollowed,
            followedShowsRepository = followedShowsRepository,
            showDetailsInteractor = ShowDetailsInteractor(
                showDetailsRepository = showDetailsRepository,
                castRepository = castRepository,
                trailerRepository = trailerRepository,
                dispatchers = coroutineDispatcher,
                providerRepository = watchProvidersRepository,
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
                seasonDetailsRepository = seasonDetailsRepository,
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
            markEpisodeUnwatchedInteractor = MarkEpisodeUnwatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            observeShowWatchProgressInteractor = ObserveShowWatchProgressInteractor(
                episodeRepository = episodeRepository,
            ),
            showContentSyncInteractor = ShowContentSyncInteractor(
                showDetailsRepository = showDetailsRepository,
                seasonDetailsRepository = seasonDetailsRepository,
                dispatchers = coroutineDispatcher,
                logger = fakeLogger,
                watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
            ),
            syncTraktCalendarInteractor = SyncTraktCalendarInteractor(
                episodeRepository = episodeRepository,
                dateTimeProvider = fakeDateTimeProvider,
                logger = FakeLogger(),
                dispatchers = coroutineDispatcher,
            ),
            scheduleEpisodeNotificationsInteractor = ScheduleEpisodeNotificationsInteractor(
                datastoreRepository = fakeDatastoreRepository,
                episodeRepository = episodeRepository,
                notificationManager = fakeNotificationManager,
                dateTimeProvider = fakeDateTimeProvider,
                logger = FakeLogger(),
                dispatchers = coroutineDispatcher,
            ),
            notificationManager = fakeNotificationManager,
            traktAuthRepository = traktAuthRepository,
            dispatchers = coroutineDispatcher,
            logger = fakeLogger,
        )
    }
}
