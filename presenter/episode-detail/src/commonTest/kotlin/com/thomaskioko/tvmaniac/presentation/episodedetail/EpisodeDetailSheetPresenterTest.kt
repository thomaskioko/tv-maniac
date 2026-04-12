package com.thomaskioko.tvmaniac.presentation.episodedetail

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.db.EpisodeId
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveEpisodeByIdInteractor
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.navigation.model.ScreenSource
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class EpisodeDetailSheetPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val episodeRepository = FakeEpisodeRepository()
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val logger = FakeLogger()

    private var navigatedToShowId: Long? = null
    private var navigatedToSeason: Triple<Long, Long, Long>? = null
    private var sheetDismissed = false

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit loading state given no episode data`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            val initialState = awaitItem()
            initialState.isLoading shouldBe true
            initialState.episodeTitle shouldBe ""
        }
    }

    @Test
    fun `should emit episode data given episode exists`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()

            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()

            state.isLoading shouldBe false
            state.episodeTitle shouldBe "The Pilot"
            state.showName shouldBe "Breaking Bad"
            state.seasonEpisodeNumber shouldBe "S1E1"
            state.imageUrl shouldBe "https://image.url/episode.jpg"
            state.overview shouldBe "A chemistry teacher begins cooking meth."
            state.rating shouldBe 9.5
            state.voteCount shouldBe 1000L
            state.isWatched shouldBe false
        }
    }

    @Test
    fun `should show all actions given source is DISCOVER`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter(source = ScreenSource.DISCOVER)

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()

            state.availableActions shouldContainExactly listOf(
                EpisodeSheetActionItem.TOGGLE_WATCHED,
                EpisodeSheetActionItem.OPEN_SHOW,
                EpisodeSheetActionItem.OPEN_SEASON,
                EpisodeSheetActionItem.UNFOLLOW,
            )
        }
    }

    @Test
    fun `should show all actions given source is UP_NEXT`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter(source = ScreenSource.UP_NEXT)

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()

            state.availableActions shouldContainExactly listOf(
                EpisodeSheetActionItem.TOGGLE_WATCHED,
                EpisodeSheetActionItem.OPEN_SHOW,
                EpisodeSheetActionItem.OPEN_SEASON,
                EpisodeSheetActionItem.UNFOLLOW,
            )
        }
    }

    @Test
    fun `should show all actions given source is CALENDAR`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter(source = ScreenSource.CALENDAR)

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()

            state.availableActions shouldContainExactly listOf(
                EpisodeSheetActionItem.TOGGLE_WATCHED,
                EpisodeSheetActionItem.OPEN_SHOW,
                EpisodeSheetActionItem.OPEN_SEASON,
                EpisodeSheetActionItem.UNFOLLOW,
            )
        }
    }

    @Test
    fun `should show only toggle watched given source is SEASON_DETAILS`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter(source = ScreenSource.SEASON_DETAILS)

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()

            state.availableActions shouldContainExactly listOf(
                EpisodeSheetActionItem.TOGGLE_WATCHED,
            )
        }
    }

    @Test
    fun `should mark episode as watched given ToggleWatched is dispatched and episode is unwatched`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(isWatched = false))

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem()

            presenter.dispatch(EpisodeDetailSheetAction.ToggleWatched)
            testDispatcher.scheduler.advanceUntilIdle()

            val call = episodeRepository.lastMarkEpisodeWatchedCall
            call shouldBe com.thomaskioko.tvmaniac.episodes.testing.MarkEpisodeWatchedCall(
                showTraktId = 100L,
                episodeId = 1L,
                seasonNumber = 1L,
                episodeNumber = 1L,
            )
            episodeRepository.lastMarkEpisodeUnwatchedCall.shouldBeNull()
        }
    }

    @Test
    fun `should mark episode as unwatched given ToggleWatched is dispatched and episode is watched`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(isWatched = true))

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem()

            presenter.dispatch(EpisodeDetailSheetAction.ToggleWatched)
            testDispatcher.scheduler.advanceUntilIdle()

            val call = episodeRepository.lastMarkEpisodeUnwatchedCall
            call shouldBe com.thomaskioko.tvmaniac.episodes.testing.MarkEpisodeUnwatchedCall(
                showTraktId = 100L,
                episodeId = 1L,
            )
        }
    }

    @Test
    fun `should navigate to show details and dismiss given OpenShow is dispatched`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem()

            presenter.dispatch(EpisodeDetailSheetAction.OpenShow)

            navigatedToShowId shouldBe 100L
        }
    }

    @Test
    fun `should navigate to season details and dismiss given OpenSeason is dispatched`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem()

            presenter.dispatch(EpisodeDetailSheetAction.OpenSeason)

            navigatedToSeason shouldBe Triple(100L, 10L, 1L)
        }
    }

    @Test
    fun `should unfollow show and dismiss given Unfollow is dispatched`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem()

            presenter.dispatch(EpisodeDetailSheetAction.Unfollow)
            testDispatcher.scheduler.advanceUntilIdle()

            followedShowsRepository.removedShowIds shouldContainExactly listOf(100L)
            sheetDismissed shouldBe true
        }
    }

    @Test
    fun `should dismiss sheet given Dismiss is dispatched`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem()

            presenter.dispatch(EpisodeDetailSheetAction.Dismiss)

            sheetDismissed shouldBe true
        }
    }

    @Test
    fun `should hide zero rating and vote count`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(rating = 0.0, voteCount = 0))

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()

            state.rating.shouldBeNull()
            state.voteCount.shouldBeNull()
        }
    }

    @Test
    fun `should set overview to null given blank overview`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(overview = ""))

        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()

            state.overview.shouldBeNull()
        }
    }

    private fun createPresenter(
        source: ScreenSource = ScreenSource.DISCOVER,
    ): EpisodeDetailSheetPresenter {
        navigatedToShowId = null
        navigatedToSeason = null
        sheetDismissed = false

        val dispatchers = AppCoroutineDispatchers(
            main = testDispatcher,
            io = testDispatcher,
            computation = testDispatcher,
            databaseWrite = testDispatcher,
            databaseRead = testDispatcher,
        )

        return EpisodeDetailSheetPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            episodeId = 1L,
            source = source,
            navigator = object : EpisodeDetailNavigator {
                override fun showDetails(showTraktId: Long) {
                    navigatedToShowId = showTraktId
                }
                override fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long) {
                    navigatedToSeason = Triple(showTraktId, seasonId, seasonNumber)
                }
                override fun dismiss() {
                    sheetDismissed = true
                }
            },
            observeEpisodeByIdInteractor = ObserveEpisodeByIdInteractor(episodeRepository),
            markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(episodeRepository),
            markEpisodeUnwatchedInteractor = MarkEpisodeUnwatchedInteractor(episodeRepository),
            unfollowShowInteractor = UnfollowShowInteractor(followedShowsRepository),
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = logger,
        )
    }

    private fun testEpisode(
        isWatched: Boolean = false,
        rating: Double = 9.5,
        voteCount: Long = 1000L,
        overview: String = "A chemistry teacher begins cooking meth.",
    ) = EpisodeById(
        episode_id = Id<EpisodeId>(1L),
        season_id = Id<SeasonId>(10L),
        show_trakt_id = Id<TraktId>(100L),
        episode_number = 1L,
        title = "The Pilot",
        overview = overview,
        vote_count = voteCount,
        ratings = rating,
        image_url = "https://image.url/episode.jpg",
        season_number = 1L,
        show_name = "Breaking Bad",
        is_watched = if (isWatched) 1L else 0L,
    )
}
