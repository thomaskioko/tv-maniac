package com.thomaskioko.tvmaniac.watchdateselection.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.domain.episode.MarkWatchedAtInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveEpisodeByIdInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ShouldPromptForRatingInteractor
import com.thomaskioko.tvmaniac.domain.rewatch.WatchAgainInteractor
import com.thomaskioko.tvmaniac.episodes.api.WatchedDate
import com.thomaskioko.tvmaniac.episodes.api.WatchedDateTarget
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetRoute
import com.thomaskioko.tvmaniac.subscription.testing.FakeSubscriptionManager
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchdateselection.nav.WatchDateSelectionParam
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

internal class WatchDateSelectionPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val appCoroutineScope = CoroutineScope(testDispatcher + SupervisorJob())
    private val episodeRepository = FakeEpisodeRepository()
    private val rewatchRepository = FakeRewatchRepository()
    private val ratingsRepository = FakeRatingsRepository()
    private val datastoreRepository = FakeDatastoreRepository()
    private val navigator = FakeNavigator()
    private val localizer = FakeLocalizer()
    private val logger = FakeLogger()
    private val dateTimeProvider = FakeDateTimeProvider()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dateTimeProvider.setCurrentTimeMillis(NOW_MILLIS)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        navigator.reset()
    }

    @Test
    fun `should write the current time given just now is selected`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(WatchDateSelectionAction.JustNowSelected)
        testDispatcher.scheduler.advanceUntilIdle()

        val call = episodeRepository.lastMarkEpisodeWatchedCall
        call?.watchedAt shouldBe NOW_MILLIS
        call?.useReleaseDate shouldBe false
    }

    @Test
    fun `should ask for the release date given release date is selected`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(firstAired = AIR_DATE_MILLIS))

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(WatchDateSelectionAction.ReleaseDateSelected)
        testDispatcher.scheduler.advanceUntilIdle()

        val call = episodeRepository.lastMarkEpisodeWatchedCall
        call?.useReleaseDate shouldBe true
        call?.watchedAt.shouldBeNull()
    }

    @Test
    fun `should write the unknown sentinel given unknown date is selected`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(WatchDateSelectionAction.UnknownDateSelected)
        testDispatcher.scheduler.advanceUntilIdle()

        episodeRepository.lastMarkEpisodeWatchedCall?.watchedAt shouldBe WatchedDate.UNKNOWN_MILLIS
    }

    @Test
    fun `should compose the picked date and time in the system zone`() = runTest {
        dateTimeProvider.setTimeZone(BERLIN)
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(
            WatchDateSelectionAction.OtherDateSelected(
                date = LocalDate(2026, 1, 15),
                time = LocalTime(20, 30),
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        episodeRepository.lastMarkEpisodeWatchedCall?.watchedAt shouldBe
            LocalDateTime(2026, 1, 15, 20, 30).toInstant(BERLIN).toEpochMilliseconds()
    }

    @Test
    fun `should compose the picked date and time given the zone is on summer time`() = runTest {
        dateTimeProvider.setTimeZone(BERLIN)
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(
            WatchDateSelectionAction.OtherDateSelected(
                date = LocalDate(2026, 7, 15),
                time = LocalTime(20, 30),
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val summer = LocalDateTime(2026, 7, 15, 20, 30).toInstant(BERLIN).toEpochMilliseconds()
        val winter = LocalDateTime(2026, 1, 15, 20, 30).toInstant(BERLIN).toEpochMilliseconds()
        episodeRepository.lastMarkEpisodeWatchedCall?.watchedAt shouldBe summer
        (winter - summer) % HOUR_MILLIS shouldBe 0L
        summer shouldBe winter + DAYS_JAN_TO_JUL * DAY_MILLIS - HOUR_MILLIS
    }

    @Test
    fun `should clamp to the current time given a date in the future is picked`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(
            WatchDateSelectionAction.OtherDateSelected(
                date = LocalDate(2030, 1, 1),
                time = LocalTime(12, 0),
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        episodeRepository.lastMarkEpisodeWatchedCall?.watchedAt shouldBe NOW_MILLIS
    }

    @Test
    fun `should disable the release date option given the episode has no air date`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(firstAired = null))

        val presenter = createPresenter()
        settle(presenter)

        presenter.state.value.isReleaseDateEnabled shouldBe false
    }

    @Test
    fun `should enable the release date option given the episode has an air date`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(firstAired = AIR_DATE_MILLIS))

        val presenter = createPresenter()
        settle(presenter)

        presenter.state.value.isReleaseDateEnabled shouldBe true
    }

    @Test
    fun `should show the current date given the sheet is correcting a dated mark`() = runTest {
        dateTimeProvider.setEpochToDisplayDateTimeResult("12 Jan 2026 20:30")
        episodeRepository.setEpisodeById(testEpisode(isWatched = true, watchedAt = AIR_DATE_MILLIS))

        val presenter = createPresenter(param = episodeParam(isEdit = true))
        settle(presenter)

        presenter.state.value.currentWatchedAtLabel shouldBe "12 Jan 2026 20:30"
    }

    @Test
    fun `should show the unknown label given the sheet is correcting an undated mark`() = runTest {
        dateTimeProvider.setEpochToDisplayDateTimeResult("1 Jan 1970 00:00")
        episodeRepository.setEpisodeById(
            testEpisode(isWatched = true, watchedAt = WatchedDate.UNKNOWN_MILLIS),
        )

        val presenter = createPresenter(param = episodeParam(isEdit = true))
        settle(presenter)

        presenter.state.value.currentWatchedAtLabel shouldBe
            localizer.getString(StringResourceKey.LabelWatchedDateUnknownDisplay)
    }

    @Test
    fun `should dismiss the sheet given the write succeeds`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(WatchDateSelectionAction.JustNowSelected)
        testDispatcher.scheduler.advanceUntilIdle()

        navigator.overlayDismissCount shouldBe 1
    }

    @Test
    fun `should open the rating sheet given quick rate is on and the episode was unwatched`() = runTest {
        datastoreRepository.saveQuickRateEnabled(true)
        episodeRepository.setEpisodeById(testEpisode(isWatched = false))

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(WatchDateSelectionAction.JustNowSelected)
        testDispatcher.scheduler.advanceUntilIdle()

        navigator.lastActivatedOverlay.shouldBeInstanceOf<RatingSheetRoute>().param.id shouldBe EPISODE_ID
    }

    @Test
    fun `should not open the rating sheet given the episode is watched again`() = runTest {
        datastoreRepository.saveQuickRateEnabled(true)
        episodeRepository.setEpisodeById(testEpisode(isWatched = true))

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(WatchDateSelectionAction.JustNowSelected)
        testDispatcher.scheduler.advanceUntilIdle()

        navigator.activatedOverlays.shouldBeEmpty()
    }

    @Test
    fun `should still dismiss the sheet given the write fails`() = runTest {
        episodeRepository.setEpisodeById(testEpisode())
        episodeRepository.setMarkEpisodeWatchedBehavior { throw IllegalStateException("Disk is full") }

        val presenter = createPresenter()
        settle(presenter)

        presenter.dispatch(WatchDateSelectionAction.JustNowSelected)
        testDispatcher.scheduler.advanceUntilIdle()

        navigator.overlayDismissCount shouldBe 1
    }

    @Test
    fun `should dismiss the sheet given the sheet is dismissed`() = runTest {
        val presenter = createPresenter()

        presenter.dispatch(WatchDateSelectionAction.Dismissed)

        navigator.overlayDismissCount shouldBe 1
        episodeRepository.lastMarkEpisodeWatchedCall.shouldBeNull()
    }

    private suspend fun settle(presenter: WatchDateSelectionPresenter) {
        presenter.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createPresenter(
        param: WatchDateSelectionParam = episodeParam(),
    ): WatchDateSelectionPresenter = WatchDateSelectionPresenter(
        param = param,
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        observeEpisodeByIdInteractor = ObserveEpisodeByIdInteractor(episodeRepository),
        markWatchedAtInteractor = MarkWatchedAtInteractor(
            episodeRepository = episodeRepository,
            watchAgainInteractor = WatchAgainInteractor(rewatchRepository),
            dateTimeProvider = dateTimeProvider,
        ),
        shouldPromptForRatingInteractor = ShouldPromptForRatingInteractor(
            datastoreRepository = datastoreRepository,
            subscriptionManager = FakeSubscriptionManager(),
            ratingsRepository = ratingsRepository,
        ),
        dateTimeProvider = dateTimeProvider,
        navigator = navigator,
        localizer = localizer,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        logger = logger,
        appScopeLauncher = FakeAppScopeLauncher(appCoroutineScope),
    )

    private fun episodeParam(isEdit: Boolean = false) = WatchDateSelectionParam(
        target = WatchedDateTarget.EPISODE,
        showId = SHOW_ID,
        episodeId = EPISODE_ID,
        seasonNumber = 1L,
        episodeNumber = 1L,
        isEdit = isEdit,
    )

    private fun testEpisode(
        isWatched: Boolean = false,
        firstAired: Long? = AIR_DATE_MILLIS,
        watchedAt: Long? = null,
    ) = EpisodeById(
        episode_id = Id(EPISODE_ID),
        season_id = Id(10L),
        show_id = Id(SHOW_ID),
        episode_number = 1L,
        title = "The Pilot",
        overview = "A chemistry teacher begins cooking meth.",
        vote_count = 1000L,
        ratings = 9.5,
        image_url = "https://image.url/episode.jpg",
        runtime = 45L,
        first_aired = firstAired,
        season_number = 1L,
        show_name = "Breaking Bad",
        is_watched = if (isWatched) 1L else 0L,
        watched_at = watchedAt,
    )

    private companion object {
        private const val SHOW_ID = 100L
        private const val EPISODE_ID = 1L
        private const val HOUR_MILLIS = 3_600_000L
        private const val DAY_MILLIS = 86_400_000L
        private const val DAYS_JAN_TO_JUL = 181L
        private val NOW_MILLIS = Instant.parse("2026-08-15T10:00:00Z").toEpochMilliseconds()
        private val AIR_DATE_MILLIS = Instant.parse("2026-01-12T18:00:00Z").toEpochMilliseconds()
        private val BERLIN = TimeZone.of("Europe/Berlin")
    }
}
