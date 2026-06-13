package com.thomaskioko.tvmaniac.presentation.upnext

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.SyncContinueWatchingInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextSortOption
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
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

internal class UpNextPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val appCoroutineScope = CoroutineScope(testDispatcher + SupervisorJob())
    private val episodeRepository = FakeEpisodeRepository()
    private val upNextRepository = FakeUpNextRepository()
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val accountManager = FakeAccountManager()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val logger = FakeLogger()
    private val syncObserver = FakeSyncObserver()
    private val navigator = FakeNavigator()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit initial state with default sort option`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            val initialState = awaitItem()
            initialState.sortOption shouldBe UpNextSortOption.LAST_WATCHED
            initialState.episodes shouldHaveSize 0
            initialState.isEmpty shouldBe true
        }
    }

    @Test
    fun `should display episodes given episodes are available`() = runTest {
        val episodes = listOf(
            createTestNextEpisode(showId = 1, showName = "Show 1"),
            createTestNextEpisode(showId = 2, showName = "Show 2"),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.episodes shouldHaveSize 2
            state.episodes[0].showName shouldBe "Show 1"
            state.episodes[1].showName shouldBe "Show 2"
            state.isEmpty shouldBe false
        }
    }

    @Test
    fun `should sort episodes by last watched given sort option is LAST_WATCHED`() = runTest {
        val episodes = listOf(
            createTestNextEpisode(showId = 1, showName = "Old Show", lastWatchedAt = 1000L),
            createTestNextEpisode(showId = 2, showName = "New Show", lastWatchedAt = 2000L),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)
        upNextRepository.setUpNextSortOption(UpNextSortOption.LAST_WATCHED.name)

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.sortOption shouldBe UpNextSortOption.LAST_WATCHED
            state.episodes shouldHaveSize 2
            state.episodes[0].showName shouldBe "New Show"
            state.episodes[1].showName shouldBe "Old Show"
        }
    }

    @Test
    fun `should sort episodes by air date given sort option is AIR_DATE`() = runTest {
        val episodes = listOf(
            createTestNextEpisode(showId = 1, showName = "Old Episode", firstAired = 1000L),
            createTestNextEpisode(showId = 2, showName = "New Episode", firstAired = 2000L),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)
        upNextRepository.setUpNextSortOption(UpNextSortOption.AIR_DATE.name)

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.sortOption shouldBe UpNextSortOption.AIR_DATE
            state.episodes shouldHaveSize 2
            state.episodes[0].showName shouldBe "New Episode"
            state.episodes[1].showName shouldBe "Old Episode"
        }
    }

    @Test
    fun `should include future episodes given firstAired is after current time`() = runTest {
        dateTimeProvider.setCurrentTimeMillis(5000L)

        val episodes = listOf(
            createTestNextEpisode(showId = 1, showName = "Aired Show", firstAired = 3000L),
            createTestNextEpisode(showId = 2, showName = "Future Show", firstAired = 10000L),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.episodes shouldHaveSize 2
            state.episodes[0].showName shouldBe "Aired Show"
            state.episodes[1].showName shouldBe "Future Show"
        }
    }

    @Test
    fun `should include episodes given firstAired is null`() = runTest {
        dateTimeProvider.setCurrentTimeMillis(5000L)

        val episodes = listOf(
            createTestNextEpisode(showId = 1, showName = "No Air Date", firstAired = null),
            createTestNextEpisode(showId = 2, showName = "Aired Show", firstAired = 3000L),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.episodes shouldHaveSize 2
        }
    }

    @Test
    fun `should update sort option given ChangeSortOption action is dispatched`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(emptyList())

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem().sortOption shouldBe UpNextSortOption.LAST_WATCHED

            presenter.dispatch(UpNextChangeSortOption(UpNextSortOption.AIR_DATE))
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = awaitItem()
            updatedState.sortOption shouldBe UpNextSortOption.AIR_DATE
        }
    }

    @Test
    fun `should record mark watched call given MarkWatched action is dispatched`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()

            presenter.dispatch(
                MarkWatched(
                    showId = 123L,
                    episodeId = 456L,
                    seasonNumber = 1L,
                    episodeNumber = 5L,
                ),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            val call = episodeRepository.lastMarkEpisodeWatchedCall
            call?.showId shouldBe 123L
            call?.episodeId shouldBe 456L
            call?.seasonNumber shouldBe 1L
            call?.episodeNumber shouldBe 5L

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to season details given ShowClicked action is dispatched`() = runTest {
        val episode = createTestNextEpisode(showId = 999, showName = "Test Show")
        upNextRepository.setNextEpisodesForWatchlist(listOf(episode))

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            awaitItem()

            presenter.dispatch(UpNextShowClicked(showId = 999L))

            navigator.lastNavigatedRoute shouldBe SeasonDetailsRoute(
                SeasonDetailsUiParam(
                    showId = 999L,
                    seasonId = 9990L,
                    seasonNumber = 1L,
                ),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should remove episode from list given episode is marked as watched`() = runTest {
        val episode1 = createTestNextEpisode(showId = 1, showName = "Show 1")
        val episode2 = createTestNextEpisode(showId = 2, showName = "Show 2")
        upNextRepository.setNextEpisodesForWatchlist(listOf(episode1, episode2))

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)

            val initialState = awaitItem()
            initialState.episodes shouldHaveSize 2
            initialState.episodes.any { it.showId == 1L } shouldBe true
            initialState.episodes.any { it.showId == 2L } shouldBe true

            presenter.dispatch(
                MarkWatched(
                    showId = episode1.showId,
                    episodeId = 100L,
                    seasonNumber = 1L,
                    episodeNumber = 1L,
                ),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            upNextRepository.setNextEpisodesForWatchlist(listOf(episode2))
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedState = expectMostRecentItem()
            updatedState.episodes shouldHaveSize 1
            updatedState.episodes.any { it.showId == 1L } shouldBe false
            updatedState.episodes.any { it.showId == 2L } shouldBe true
            updatedState.updatingEpisodeIds.shouldBeEmpty()
        }
    }

    @Test
    fun `should populate updatingEpisodeIds while mark watched is in flight`() = runTest {
        val episode = createTestNextEpisode(showId = 1, showName = "Show 1")
        upNextRepository.setNextEpisodesForWatchlist(listOf(episode))

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            val initial = awaitItem()
            initial.updatingEpisodeIds.shouldBeEmpty()

            presenter.dispatch(
                MarkWatched(
                    showId = episode.showId,
                    episodeId = 100L,
                    seasonNumber = 1L,
                    episodeNumber = 1L,
                ),
            )

            val inFlight = awaitItem()
            inFlight.updatingEpisodeIds shouldContain 100L

            testDispatcher.scheduler.advanceUntilIdle()
            val settled = expectMostRecentItem()
            settled.updatingEpisodeIds.shouldBeEmpty()
        }
    }

    @Test
    fun `should map all episode fields to ui model correctly`() = runTest {
        val episode = NextEpisodeWithShow(
            showId = 84L,
            episodeId = 100L,
            episodeName = "Pilot",
            seasonId = 10L,
            seasonNumber = 1L,
            episodeNumber = 1L,
            runtime = 60L,
            stillPath = "/still.jpg",
            overview = "A great episode",
            showName = "Test Show",
            showPoster = "/poster.jpg",
            showStatus = "Returning Series",
            showYear = "2025",
            firstAired = 1000L,
            lastWatchedAt = 2000L,
            seasonCount = 5L,
            episodeCount = 12L,
            watchedCount = 8L,
            totalCount = 60L,
        )
        upNextRepository.setNextEpisodesForWatchlist(listOf(episode))

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.episodes shouldHaveSize 1

            val uiModel = state.episodes[0]
            uiModel.showId shouldBe 84L
            uiModel.episodeId shouldBe 100L
            uiModel.episodeName shouldBe "Pilot"
            uiModel.seasonId shouldBe 10L
            uiModel.seasonNumber shouldBe 1L
            uiModel.episodeNumber shouldBe 1L
            uiModel.runtime shouldBe 60L
            uiModel.imageUrl shouldBe "/still.jpg"
            uiModel.overview shouldBe "A great episode"
            uiModel.showName shouldBe "Test Show"
            uiModel.showStatus shouldBe "Returning Series"
            uiModel.showYear shouldBe "2025"
            uiModel.firstAired shouldBe 1000L
            uiModel.seasonCount shouldBe 5L
            uiModel.episodeCount shouldBe 12L
            uiModel.watchedCount shouldBe 8L
            uiModel.totalCount shouldBe 60L
        }
    }

    @Test
    fun `should refresh data given auth state changes to logged in`() = runTest {
        val episodes = listOf(
            createTestNextEpisode(showId = 1, showName = "Show 1"),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)

            val state = awaitItem()
            state.episodes shouldHaveSize 1

            accountManager.setActiveProvider(AccountProvider.TRAKT)
            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should not crash given RefreshUpNext action is dispatched`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(emptyList())
        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()

            presenter.dispatch(RefreshUpNext)
            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should show loading given sync in progress and no episodes`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(emptyList())
        syncObserver.setSyncing(true)

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.isSyncing shouldBe true
            state.isEmpty shouldBe true
            state.showLoading shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should not show loading given sync in progress and episodes present`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(
            listOf(createTestNextEpisode(showId = 1, showName = "Show 1")),
        )
        syncObserver.setSyncing(true)

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.isSyncing shouldBe true
            state.isEmpty shouldBe false
            state.showLoading shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createPresenter(): UpNextPresenter {
        val observeUpNextInteractor = ObserveUpNextInteractor(
            repository = upNextRepository,
        )

        val dispatchers = AppCoroutineDispatchers(
            main = testDispatcher,
            io = testDispatcher,
            computation = testDispatcher,
            databaseWrite = testDispatcher,
            databaseRead = testDispatcher,
        )

        val syncContinueWatchingInteractor = SyncContinueWatchingInteractor(
            syncActivityInteractor = SyncActivityInteractor(
                traktActivityRepository = FakeTraktActivityRepository(),
                dispatchers = dispatchers,
            ),
            continueWatchingRepository = FakeContinueWatchingRepository(),
            syncShowMetadataInteractor = SyncShowMetadataInteractor(
                showDetailsRepository = FakeShowDetailsRepository(),
                seasonDetailsRepository = FakeSeasonDetailsRepository(),
                watchProviderRepository = FakeWatchProviderRepository(),
                dispatchers = dispatchers,
            ),
            watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository(),
            activeProviderFeatures = { FakeProviderFeatures(supportsContinueWatchingFetch = true) },
            requestManagerRepository = FakeRequestManagerRepository(initialRequestValid = false),
            dispatchers = dispatchers,
            logger = logger,
        )

        val markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
            episodeRepository = episodeRepository,
        )

        return UpNextPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigator = navigator,
            observeUpNextInteractor = observeUpNextInteractor,
            syncContinueWatchingInteractor = syncContinueWatchingInteractor,
            markEpisodeWatchedInteractor = markEpisodeWatchedInteractor,
            upNextRepository = upNextRepository,
            unfollowShowInteractor = UnfollowShowInteractor(
                followedShowsRepository = followedShowsRepository,
                libraryRepository = FakeLibraryRepository(),
                appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
            ),
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = logger,
            syncObserver = syncObserver,
        )
    }

    private fun createTestNextEpisode(
        showId: Long,
        showName: String,
        lastWatchedAt: Long? = null,
        firstAired: Long? = null,
    ): NextEpisodeWithShow = NextEpisodeWithShow(
        showId = showId,
        episodeId = showId * 100,
        episodeName = "Episode 1",
        seasonId = showId * 10,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = 45L,
        stillPath = null,
        overview = "Test overview",
        showName = showName,
        showPoster = null,
        showStatus = "Returning Series",
        showYear = "2024",
        firstAired = firstAired,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 3L,
        episodeCount = 10L,
        watchedCount = 5L,
        totalCount = 30L,
    )
}
