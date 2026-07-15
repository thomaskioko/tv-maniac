package com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveShowWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.episode.SyncShowEpisodeWatchesInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FetchSeasonsEpisodesInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveContinueTrackingInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveSeasonsInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.testContinueTrackingResult
import com.thomaskioko.tvmaniac.presenter.showdetails.testSeasonWatchProgress
import com.thomaskioko.tvmaniac.presenter.showdetails.testSeasonsWithProgress
import com.thomaskioko.tvmaniac.presenter.showdetails.testShowWatchProgress
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowSeasonDetailsParam
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

internal class ShowDetailsSeasonsEpisodesPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val seasonsRepository = FakeSeasonsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val accountManager = FakeAccountManager()
    private val navigator = FakeNavigator()
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val datastoreRepository = FakeDatastoreRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        followedShowsRepository.setEntries(
            listOf(FollowedShowEntry(showId = SHOW_ID, followedAt = Instant.fromEpochMilliseconds(0))),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should map seasons with per season progress given seasons and progress data`() = runTest {
        seasonsRepository.setSeasonsResult(testSeasonsWithProgress)
        episodeRepository.setAllSeasonsWatchProgress(testSeasonWatchProgress)
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.seasonsList.size shouldBe 2
            state.numberOfSeasons shouldBe 2
            state.seasonsList[0].watchedCount shouldBe 8
            state.seasonsList[0].totalCount shouldBe 10
            state.seasonsList[1].watchedCount shouldBe 3
            state.seasonsList[1].totalCount shouldBe 12
        }
    }

    @Test
    fun `should reverse seasons given newest season first order`() = runTest {
        seasonsRepository.setSeasonsResult(testSeasonsWithProgress)
        datastoreRepository.saveSeasonSortOrder(SeasonSortOrder.NEWEST_FIRST)
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.seasonsList.size shouldBe 2
            state.seasonsList[0].seasonNumber shouldBe 2
            state.seasonsList[1].seasonNumber shouldBe 1
        }
    }

    @Test
    fun `should keep seasons oldest first given default order`() = runTest {
        seasonsRepository.setSeasonsResult(testSeasonsWithProgress)
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.seasonsList[0].seasonNumber shouldBe 1
            state.seasonsList[1].seasonNumber shouldBe 2
        }
    }

    @Test
    fun `should expose overall watch progress given progress data`() = runTest {
        episodeRepository.setShowWatchProgress(testShowWatchProgress)
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.watchedEpisodesCount shouldBe 5
            state.totalEpisodesCount shouldBe 10
            state.watchProgress shouldBe 0.5f
        }
    }

    @Test
    fun `should map continue tracking episodes and compute scroll index`() = runTest {
        seasonDetailsRepository.setContinueTrackingResult(testContinueTrackingResult)
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.continueTrackingEpisodes.size shouldBe 3
            state.continueTrackingScrollIndex shouldBe 0
        }
    }

    @Test
    fun `should clear continue tracking episodes given null result`() = runTest {
        seasonDetailsRepository.setContinueTrackingResult(null)
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            expectMostRecentItem().continueTrackingEpisodes.shouldBeEmpty()
        }
    }

    @Test
    fun `should set selected season index and navigate given season clicked`() = runTest {
        seasonsRepository.setSeasonsResult(testSeasonsWithProgress)
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(
            ShowDetailsSeasonClicked(
                params = ShowSeasonDetailsParam(
                    showId = SHOW_ID,
                    seasonId = 101L,
                    seasonNumber = 1L,
                    selectedSeasonIndex = 1,
                ),
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().selectedSeasonIndex shouldBe 1
        }
        navigator.lastNavigatedRoute.shouldBeInstanceOf<SeasonDetailsRoute>()
    }

    @Test
    fun `should optimistically flag episode as updating given mark watched dispatched`() = runTest {
        seasonDetailsRepository.setContinueTrackingResult(testContinueTrackingResult)
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().updatingEpisodeIds.shouldBeEmpty()

            presenter.dispatch(
                ShowDetailsMarkEpisodeWatched(showId = SHOW_ID, episodeId = 1001L, seasonNumber = 1L, episodeNumber = 1L),
            )
            awaitItem().updatingEpisodeIds shouldContain 1001L

            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().updatingEpisodeIds.shouldBeEmpty()
        }
    }

    @Test
    fun `should clear updating flag after mark watched completes`() = runTest {
        seasonDetailsRepository.setContinueTrackingResult(testContinueTrackingResult)
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            presenter.dispatch(
                ShowDetailsMarkEpisodeWatched(showId = SHOW_ID, episodeId = 1001L, seasonNumber = 1L, episodeNumber = 1L),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            expectMostRecentItem().updatingEpisodeIds.shouldBeEmpty()
        }
    }

    @Test
    fun `should optimistically flag episode as updating given mark unwatched dispatched`() = runTest {
        seasonDetailsRepository.setContinueTrackingResult(testContinueTrackingResult)
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().updatingEpisodeIds.shouldBeEmpty()

            presenter.dispatch(ShowDetailsMarkEpisodeUnwatched(showId = SHOW_ID, episodeId = 1001L))
            awaitItem().updatingEpisodeIds shouldContain 1001L

            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().updatingEpisodeIds.shouldBeEmpty()
        }
    }

    @Test
    fun `should sync only watch status given trakt connects`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val seasonSyncCountBefore = seasonDetailsRepository.getSyncedShowIds().size
            val watchSyncCountBefore = watchedEpisodeSyncRepository.getSyncedShowIds().size

            accountManager.setActiveProvider(SyncProviderSource.TRAKT)
            testDispatcher.scheduler.advanceUntilIdle()

            watchedEpisodeSyncRepository.getSyncedShowIds().size shouldBe watchSyncCountBefore + 1
            watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe true
            seasonDetailsRepository.getSyncedShowIds().size shouldBe seasonSyncCountBefore

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun buildPresenter(): ShowDetailsSeasonsEpisodesPresenter =
        ShowDetailsSeasonsEpisodesPresenter(
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry().apply { resume() }),
            showId = SHOW_ID,
            forceRefresh = false,
            observeSeasonsInteractor = ObserveSeasonsInteractor(
                seasonsRepository = seasonsRepository,
                episodeRepository = episodeRepository,
                dispatchers = dispatchers,
            ),
            observeShowWatchProgressInteractor = ObserveShowWatchProgressInteractor(
                episodeRepository = episodeRepository,
            ),
            observeContinueTrackingInteractor = ObserveContinueTrackingInteractor(
                seasonDetailsRepository = seasonDetailsRepository,
                followedShowsRepository = followedShowsRepository,
                dispatchers = dispatchers,
            ),
            fetchSeasonsEpisodesInteractor = FetchSeasonsEpisodesInteractor(
                showDetailsRepository = showDetailsRepository,
                seasonDetailsRepository = seasonDetailsRepository,
                watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
                dispatchers = dispatchers,
            ),
            syncShowEpisodeWatchesInteractor = SyncShowEpisodeWatchesInteractor(
                watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
                dispatchers = dispatchers,
            ),
            markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            markEpisodeUnwatchedInteractor = MarkEpisodeUnwatchedInteractor(
                episodeRepository = episodeRepository,
            ),
            datastoreRepository = datastoreRepository,
            navigator = navigator,
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

    private companion object {
        private const val SHOW_ID = 84958L
    }
}
