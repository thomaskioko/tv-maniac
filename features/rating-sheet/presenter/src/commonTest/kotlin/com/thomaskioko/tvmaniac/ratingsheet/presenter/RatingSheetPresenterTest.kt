package com.thomaskioko.tvmaniac.ratingsheet.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonById
import com.thomaskioko.tvmaniac.db.TvshowDetails
import com.thomaskioko.tvmaniac.domain.ratings.ObserveRatingInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ObserveRatingTargetInteractor
import com.thomaskioko.tvmaniac.domain.ratings.RateInteractor
import com.thomaskioko.tvmaniac.domain.ratings.RemoveRatingInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetParam
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import io.kotest.matchers.nulls.shouldBeNull
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

internal class RatingSheetPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val appCoroutineScope = CoroutineScope(testDispatcher + SupervisorJob())
    private val ratingsRepository = FakeRatingsRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonsRepository = FakeSeasonsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val navigator = FakeNavigator()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        showDetailsRepository.setShowDetailsResult(tvshowDetails(name = "Lioness", year = "2023"))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        navigator.reset()
    }

    @Test
    fun `should emit user rating given rating is observed`() = runTest {
        ratingsRepository.setShowRating(
            ShowRating(userRating = 7, communityRating = null, communityVotes = null, pendingAction = PendingAction.NOTHING),
        )

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().userRating shouldBe 7
        }
    }

    @Test
    fun `should update rating without dismissing given star selected`() = runTest {
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.state.test {
            presenter.dispatch(RatingSheetAction.RatingSelected(9))
            testDispatcher.scheduler.advanceUntilIdle()

            expectMostRecentItem().userRating shouldBe 9
            navigator.overlayDismissCount shouldBe 0
        }
    }

    @Test
    fun `should clear rating without dismissing given rating cleared`() = runTest {
        ratingsRepository.setShowRating(
            ShowRating(userRating = 7, communityRating = null, communityVotes = null, pendingAction = PendingAction.NOTHING),
        )
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.state.test {
            presenter.dispatch(RatingSheetAction.RatingCleared)
            testDispatcher.scheduler.advanceUntilIdle()

            expectMostRecentItem().userRating.shouldBeNull()
            navigator.overlayDismissCount shouldBe 0
        }
    }

    @Test
    fun `should emit show name and year given show target`() = runTest {
        showDetailsRepository.setShowDetailsResult(tvshowDetails(name = "Lioness", year = "2023"))

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.title shouldBe "Lioness"
            state.subtitle shouldBe "2023"
            state.posterUrl shouldBe "/lioness.jpg"
            state.backdropUrl.shouldBeNull()
        }
    }

    @Test
    fun `should emit show name without subtitle given show has no year`() = runTest {
        showDetailsRepository.setShowDetailsResult(tvshowDetails(name = "Lioness", year = null))

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.title shouldBe "Lioness"
            state.subtitle.shouldBeNull()
        }
    }

    @Test
    fun `should emit season title and show name given season target`() = runTest {
        seasonsRepository.setSeasonById(
            SeasonById(
                season_id = Id(2L),
                title = "Season 1",
                season_number = 1L,
                image_url = "/season-1.jpg",
                show_name = "Lioness",
            ),
        )

        val presenter = createPresenter(RatingSheetParam(ratingType = RatingEntityType.SEASON, id = 2L))

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.title shouldBe "Season 1"
            state.subtitle shouldBe "Lioness"
            state.posterUrl shouldBe "/season-1.jpg"
            state.backdropUrl.shouldBeNull()
        }
    }

    @Test
    fun `should emit episode title and show name with season and episode number given episode target`() = runTest {
        episodeRepository.setEpisodeById(
            episodeById(title = "Sacrificial Soldiers", showName = "Lioness", seasonNumber = 1L, episodeNumber = 1L),
        )

        val presenter = createPresenter(RatingSheetParam(ratingType = RatingEntityType.EPISODE, id = 3L))

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.title shouldBe "Sacrificial Soldiers"
            state.subtitle shouldBe "Lioness • S1E1"
            state.backdropUrl shouldBe "/sacrificial-soldiers.jpg"
            state.posterUrl.shouldBeNull()
        }
    }

    @Test
    fun `should dismiss overlay given dismissed`() = runTest {
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(RatingSheetAction.Dismissed)
        testDispatcher.scheduler.advanceUntilIdle()

        navigator.overlayDismissCount shouldBe 1
    }

    private fun createPresenter(
        param: RatingSheetParam = RatingSheetParam(ratingType = RatingEntityType.SHOW, id = 1L),
    ): RatingSheetPresenter =
        RatingSheetPresenter(
            param = param,
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            observeRatingInteractor = ObserveRatingInteractor(ratingsRepository),
            observeRatingTargetInteractor = ObserveRatingTargetInteractor(
                showDetailsRepository = showDetailsRepository,
                seasonsRepository = seasonsRepository,
                episodeRepository = episodeRepository,
            ),
            rateInteractor = RateInteractor(ratingsRepository),
            removeRatingInteractor = RemoveRatingInteractor(ratingsRepository),
            navigator = navigator,
            localizer = FakeLocalizer(),
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
            appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
        )

    private fun tvshowDetails(name: String, year: String?) = TvshowDetails(
        trakt_id = 1L,
        tmdb_id = Id(1L),
        name = name,
        overview = "",
        language = null,
        year = year,
        ratings = 0.0,
        status = null,
        vote_count = 0L,
        poster_path = "/lioness.jpg",
        backdrop_path = null,
        genres = null,
        season_numbers = null,
        in_library = 0L,
    )

    private fun episodeById(title: String, showName: String, seasonNumber: Long, episodeNumber: Long) = EpisodeById(
        episode_id = Id(3L),
        season_id = Id(2L),
        show_id = Id(1L),
        episode_number = episodeNumber,
        title = title,
        overview = "",
        vote_count = 0L,
        ratings = 0.0,
        image_url = "/sacrificial-soldiers.jpg",
        runtime = null,
        first_aired = null,
        season_number = seasonNumber,
        show_name = showName,
        is_watched = 0L,
        watched_at = null,
    )
}
