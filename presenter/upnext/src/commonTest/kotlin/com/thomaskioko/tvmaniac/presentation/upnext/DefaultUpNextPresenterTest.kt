package com.thomaskioko.tvmaniac.presentation.upnext

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.upnext.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.domain.upnext.RefreshUpNextInteractor
import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class DefaultUpNextPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val episodeRepository = FakeEpisodeRepository()
    private val upNextRepository = FakeUpNextRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val logger = FakeLogger()

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
            createTestNextEpisode(showTraktId = 1, showName = "Show 1"),
            createTestNextEpisode(showTraktId = 2, showName = "Show 2"),
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
            createTestNextEpisode(showTraktId = 1, showName = "Old Show", lastWatchedAt = 1000L),
            createTestNextEpisode(showTraktId = 2, showName = "New Show", lastWatchedAt = 2000L),
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
            createTestNextEpisode(showTraktId = 1, showName = "Old Episode", firstAired = 1000L),
            createTestNextEpisode(showTraktId = 2, showName = "New Episode", firstAired = 2000L),
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
    fun `should use followedAt as fallback given lastWatchedAt is null and sort is LAST_WATCHED`() = runTest {
        val episodes = listOf(
            createTestNextEpisode(showTraktId = 1, showName = "Old Follow", followedAt = 1000L),
            createTestNextEpisode(showTraktId = 2, showName = "New Follow", followedAt = 2000L),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)
        upNextRepository.setUpNextSortOption(UpNextSortOption.LAST_WATCHED.name)

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.episodes shouldHaveSize 2
            state.episodes[0].showName shouldBe "New Follow"
            state.episodes[1].showName shouldBe "Old Follow"
        }
    }

    @Test
    fun `should filter out future episodes given firstAired is after current time`() = runTest {
        dateTimeProvider.setCurrentTimeMillis(5000L)

        val episodes = listOf(
            createTestNextEpisode(showTraktId = 1, showName = "Aired Show", firstAired = 3000L),
            createTestNextEpisode(showTraktId = 2, showName = "Future Show", firstAired = 10000L),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)
            val state = awaitItem()
            state.episodes shouldHaveSize 1
            state.episodes[0].showName shouldBe "Aired Show"
        }
    }

    @Test
    fun `should include episodes given firstAired is null`() = runTest {
        dateTimeProvider.setCurrentTimeMillis(5000L)

        val episodes = listOf(
            createTestNextEpisode(showTraktId = 1, showName = "No Air Date", firstAired = null),
            createTestNextEpisode(showTraktId = 2, showName = "Aired Show", firstAired = 3000L),
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
                    showTraktId = 123L,
                    episodeId = 456L,
                    seasonNumber = 1L,
                    episodeNumber = 5L,
                ),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            val call = episodeRepository.lastMarkEpisodeWatchedCall
            call?.showTraktId shouldBe 123L
            call?.episodeId shouldBe 456L
            call?.seasonNumber shouldBe 1L
            call?.episodeNumber shouldBe 5L

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to show details given ShowClicked action is dispatched`() = runTest {
        var navigatedToShowId: Long? = null
        val presenterWithNav = createPresenter(
            navigateToShowDetails = { navigatedToShowId = it },
        )

        presenterWithNav.dispatch(UpNextShowClicked(showTraktId = 999L))

        navigatedToShowId shouldBe 999L
    }

    @Test
    fun `should remove episode from list given episode is marked as watched`() = runTest {
        val episode1 = createTestNextEpisode(showTraktId = 1, showName = "Show 1")
        val episode2 = createTestNextEpisode(showTraktId = 2, showName = "Show 2")
        upNextRepository.setNextEpisodesForWatchlist(listOf(episode1, episode2))

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)

            val initialState = awaitItem()
            initialState.episodes shouldHaveSize 2
            initialState.episodes.any { it.showTraktId == 1L } shouldBe true
            initialState.episodes.any { it.showTraktId == 2L } shouldBe true

            presenter.dispatch(
                MarkWatched(
                    showTraktId = episode1.showTraktId,
                    episodeId = episode1.episodeId,
                    seasonNumber = episode1.seasonNumber,
                    episodeNumber = episode1.episodeNumber,
                ),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            upNextRepository.setNextEpisodesForWatchlist(listOf(episode2))

            val updatedState = awaitItem()
            updatedState.episodes shouldHaveSize 1
            updatedState.episodes.any { it.showTraktId == 1L } shouldBe false
            updatedState.episodes.any { it.showTraktId == 2L } shouldBe true
        }
    }

    @Test
    fun `should map all episode fields to ui model correctly`() = runTest {
        val episode = NextEpisodeWithShow(
            showTraktId = 42L,
            showTmdbId = 84L,
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
            followedAt = 500L,
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
            uiModel.showTraktId shouldBe 42L
            uiModel.showTmdbId shouldBe 84L
            uiModel.episodeId shouldBe 100L
            uiModel.episodeName shouldBe "Pilot"
            uiModel.seasonId shouldBe 10L
            uiModel.seasonNumber shouldBe 1L
            uiModel.episodeNumber shouldBe 1L
            uiModel.runtime shouldBe 60L
            uiModel.stillPath shouldBe "/still.jpg"
            uiModel.overview shouldBe "A great episode"
            uiModel.showName shouldBe "Test Show"
            uiModel.showPoster shouldBe "/poster.jpg"
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
            createTestNextEpisode(showTraktId = 1, showName = "Show 1"),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1)

            val state = awaitItem()
            state.episodes shouldHaveSize 1

            traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should refresh data given followed shows count changes`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            upNextRepository.setFollowedShowsCount(5)
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

    private fun createPresenter(
        navigateToShowDetails: (Long) -> Unit = { },
    ): UpNextPresenter {
        val observeUpNextInteractor = ObserveUpNextInteractor(
            repository = upNextRepository,
            dateTimeProvider = dateTimeProvider,
        )

        val refreshUpNextInteractor = RefreshUpNextInteractor(
            upNextRepository = upNextRepository,
        )

        val markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
            episodeRepository = episodeRepository,
        )

        return DefaultUpNextPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigateToShowDetails = navigateToShowDetails,
            observeUpNextInteractor = observeUpNextInteractor,
            refreshUpNextInteractor = refreshUpNextInteractor,
            markEpisodeWatchedInteractor = markEpisodeWatchedInteractor,
            upNextRepository = upNextRepository,
            traktAuthRepository = traktAuthRepository,
            logger = logger,
        )
    }

    private fun createTestNextEpisode(
        showTraktId: Long,
        showName: String,
        lastWatchedAt: Long? = null,
        firstAired: Long? = null,
        followedAt: Long? = null,
    ): NextEpisodeWithShow = NextEpisodeWithShow(
        showTraktId = showTraktId,
        showTmdbId = showTraktId,
        episodeId = showTraktId * 100,
        episodeName = "Episode 1",
        seasonId = showTraktId * 10,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = 45L,
        stillPath = null,
        overview = "Test overview",
        showName = showName,
        showPoster = null,
        showStatus = "Returning Series",
        showYear = "2024",
        followedAt = followedAt,
        firstAired = firstAired,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 3L,
        episodeCount = 10L,
        watchedCount = 5L,
        totalCount = 30L,
    )
}
