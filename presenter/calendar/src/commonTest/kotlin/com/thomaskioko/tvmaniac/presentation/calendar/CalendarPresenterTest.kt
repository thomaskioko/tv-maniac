package com.thomaskioko.tvmaniac.presentation.calendar

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.testing.FakeCalendarRepository
import com.thomaskioko.tvmaniac.domain.calendar.CalendarEpisodeFormatter
import com.thomaskioko.tvmaniac.domain.calendar.CalendarWeekCalculator
import com.thomaskioko.tvmaniac.domain.calendar.FetchCalendarInteractor
import com.thomaskioko.tvmaniac.domain.calendar.ObserveCalendarInteractor
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class CalendarPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val calendarRepository = FakeCalendarRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val formatterUtil = FakeFormatterUtil()
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
    fun `should emit initial loading state given no data`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            val initialState = awaitItem()
            initialState.isLoading shouldBe true
            initialState.dateGroups.shouldBeEmpty()
            initialState.isLoggedIn shouldBe false
        }
    }

    @Test
    fun `should display grouped episodes given entries are available`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(showTraktId = 1, episodeTraktId = 10, airDate = airDate),
                createTestEntry(showTraktId = 2, episodeTraktId = 20, airDate = airDate),
            ),
        )

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            state.dateGroups shouldHaveSize 1
            state.dateGroups[0].episodes shouldHaveSize 2
            state.isLoading shouldBe false
        }
    }

    @Test
    fun `should group multiple episodes of the same show given same date`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 10,
                    seasonNumber = 1,
                    episodeNumber = 1,
                    airDate = airDate,
                ),
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 11,
                    seasonNumber = 1,
                    episodeNumber = 2,
                    airDate = airDate,
                ),
            ),
        )

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            state.dateGroups shouldHaveSize 1
            state.dateGroups[0].episodes shouldHaveSize 1
            state.dateGroups[0].episodes[0].additionalEpisodesCount shouldBe 1
        }
    }

    @Test
    fun `should separate entries into different date groups given different air dates`() = runTest {
        val todayEpoch = todayEpochMillis()
        val tomorrowEpoch = tomorrowEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(showTraktId = 1, episodeTraktId = 10, airDate = todayEpoch),
                createTestEntry(showTraktId = 2, episodeTraktId = 20, airDate = tomorrowEpoch),
            ),
        )

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            state.dateGroups shouldHaveSize 2
            state.dateGroups[0].episodes shouldHaveSize 1
            state.dateGroups[1].episodes shouldHaveSize 1
        }
    }

    @Test
    fun `should format episode info correctly given season and episode numbers`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 10,
                    seasonNumber = 2,
                    episodeNumber = 5,
                    episodeTitle = "The One",
                    airDate = airDate,
                ),
            ),
        )

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            state.dateGroups[0].episodes[0].episodeInfo shouldBe "S02E05 · The One"
        }
    }

    @Test
    fun `should format episode info without title given title is null`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 10,
                    seasonNumber = 1,
                    episodeNumber = 3,
                    episodeTitle = null,
                    airDate = airDate,
                ),
            ),
        )

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            state.dateGroups[0].episodes[0].episodeInfo shouldBe "S01E03"
        }
    }

    @Test
    fun `should set canNavigateNext to true given user is logged in`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.isLoggedIn shouldBe true
            state.canNavigateNext shouldBe true
            state.isRefreshing shouldBe false
        }
    }

    @Test
    fun `should set canNavigateNext to false given user is not logged in`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_OUT)
        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            state.isLoggedIn shouldBe false
            state.canNavigateNext shouldBe false
        }
    }

    @Test
    fun `should set canNavigatePrevious to false given week offset is zero`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            val state = awaitItem()
            state.canNavigatePrevious shouldBe false
        }
    }

    @Test
    fun `should set canNavigatePrevious to true given user navigated to next week`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem() // Consume settled initial state

            presenter.dispatch(NavigateToNextWeek)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.canNavigatePrevious shouldBe true
            state.isRefreshing shouldBe false
        }
    }

    @Test
    fun `should not navigate to previous week given offset is already zero`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            awaitItem()

            presenter.dispatch(NavigateToPreviousWeek)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.canNavigatePrevious shouldBe false
        }
    }

    @Test
    fun `should decrement week offset given NavigateToPreviousWeek is dispatched after navigating forward`() = runTest {
        traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem() // Consume settled initial state

            presenter.dispatch(NavigateToNextWeek)
            testDispatcher.scheduler.advanceUntilIdle()

            val afterNext = expectMostRecentItem()
            afterNext.canNavigatePrevious shouldBe true

            presenter.dispatch(NavigateToPreviousWeek)
            testDispatcher.scheduler.advanceUntilIdle()

            val afterPrev = expectMostRecentItem()
            afterPrev.canNavigatePrevious shouldBe false
            afterPrev.isRefreshing shouldBe false
        }
    }

    @Test
    fun `should invoke callback given EpisodeCardClicked is dispatched`() = runTest {
        var clickedEpisodeId: Long? = null
        val presenter = createPresenter(
            onEpisodeLongPressed = { clickedEpisodeId = it },
        )

        presenter.state.test {
            awaitItem()

            presenter.dispatch(EpisodeCardClicked(episodeTraktId = 42))

            clickedEpisodeId shouldBe 42
        }
    }

    @Test
    fun `should invoke callback with episode id given EpisodeCardClicked with unknown id`() = runTest {
        var clickedEpisodeId: Long? = null
        val presenter = createPresenter(
            onEpisodeLongPressed = { clickedEpisodeId = it },
        )

        presenter.state.test {
            awaitItem()

            presenter.dispatch(EpisodeCardClicked(episodeTraktId = 999))

            clickedEpisodeId shouldBe 999
        }
    }

    @Test
    fun `should settle to not refreshing given RefreshCalendar is dispatched`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            expectMostRecentItem() // Consume settled initial state

            presenter.dispatch(RefreshCalendar)
            testDispatcher.scheduler.advanceUntilIdle()

            val settled = expectMostRecentItem()
            settled.isRefreshing shouldBe false
        }
    }

    @Test
    fun `should map show metadata to episode item given entries are available`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 10,
                    showTitle = "Breaking Bad",
                    showPosterPath = "/poster.jpg",
                    network = "AMC",
                    overview = "A great episode",
                    rating = 9.5,
                    votes = 1000,
                    runtime = 45,
                    airDate = airDate,
                ),
            ),
        )

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            val episode = state.dateGroups[0].episodes[0]
            episode.showTraktId shouldBe 1
            episode.episodeTraktId shouldBe 10
            episode.showTitle shouldBe "Breaking Bad"
            episode.posterUrl shouldBe "/poster.jpg"
            episode.network shouldBe "AMC"
            episode.overview shouldBe "A great episode"
            episode.rating shouldBe 9.5
            episode.votes shouldBe 1000
            episode.runtime shouldBe 45
        }
    }

    @Test
    fun `should show loading false given data is available`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(createTestEntry(showTraktId = 1, episodeTraktId = 10, airDate = airDate)),
        )

        val presenter = createPresenter()

        presenter.state.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            state.isLoading shouldBe false
            state.dateGroups shouldHaveSize 1
        }
    }

    private fun createPresenter(
        navigateToShowDetails: (Long) -> Unit = {},
        onEpisodeLongPressed: (Long) -> Unit = {},
    ): CalendarPresenter {
        val dispatchers = AppCoroutineDispatchers(
            main = testDispatcher,
            io = testDispatcher,
            computation = testDispatcher,
            databaseWrite = testDispatcher,
            databaseRead = testDispatcher,
        )

        val calendarWeekCalculator = CalendarWeekCalculator(
            dateTimeProvider = dateTimeProvider,
            formatterUtil = formatterUtil,
        )

        val calendarEpisodeFormatter = CalendarEpisodeFormatter(
            formatterUtil = formatterUtil,
        )

        val observeCalendarInteractor = ObserveCalendarInteractor(
            repository = calendarRepository,
            calendarWeekCalculator = calendarWeekCalculator,
            calendarEpisodeFormatter = calendarEpisodeFormatter,
            dateTimeProvider = dateTimeProvider,
        )

        val fetchCalendarInteractor = FetchCalendarInteractor(
            repository = calendarRepository,
            dispatchers = dispatchers,
        )

        val calendarStateMapper = CalendarStateMapper(
            localizer = com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer(),
        )

        return CalendarPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigateToShowDetails = navigateToShowDetails,
            onEpisodeLongPressed = onEpisodeLongPressed,
            observeCalendarInteractor = observeCalendarInteractor,
            fetchCalendarInteractor = fetchCalendarInteractor,
            traktAuthRepository = traktAuthRepository,
            calendarWeekCalculator = calendarWeekCalculator,
            calendarStateMapper = calendarStateMapper,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = logger,
        )
    }

    private fun todayEpochMillis(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        return today.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    private fun tomorrowEpochMillis(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        return tomorrow.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    @Suppress("LongParameterList")
    private fun createTestEntry(
        showTraktId: Long = 1,
        episodeTraktId: Long = 10,
        seasonNumber: Int = 1,
        episodeNumber: Int = 1,
        episodeTitle: String? = "Test Episode",
        airDate: Long = 0L,
        showTitle: String = "Test Show",
        showPosterPath: String? = null,
        network: String? = "HBO",
        runtime: Int? = 60,
        overview: String? = "Test overview",
        rating: Double? = 8.0,
        votes: Int? = 100,
    ): CalendarEntry = CalendarEntry(
        showTraktId = showTraktId,
        episodeTraktId = episodeTraktId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        episodeTitle = episodeTitle,
        airDate = airDate,
        showTitle = showTitle,
        showPosterPath = showPosterPath,
        network = network,
        runtime = runtime,
        overview = overview,
        rating = rating,
        votes = votes,
    )
}
