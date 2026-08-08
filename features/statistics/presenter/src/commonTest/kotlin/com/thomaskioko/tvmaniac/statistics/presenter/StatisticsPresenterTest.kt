package com.thomaskioko.tvmaniac.statistics.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.domain.statistics.ObserveWatchStatisticsInteractor
import com.thomaskioko.tvmaniac.domain.statistics.WatchStatisticsCalculator
import com.thomaskioko.tvmaniac.domain.statistics.model.AverageRating
import com.thomaskioko.tvmaniac.episodes.api.model.MostWatchedShow
import com.thomaskioko.tvmaniac.episodes.api.model.WatchedEpisodeRuntime
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTileId
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.testing.FakeSubscriptionManager
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.watchstatus.testing.FakeShowWatchStatusRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

internal class StatisticsPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val episodeRepository = FakeEpisodeRepository()
    private val showWatchStatusRepository = FakeShowWatchStatusRepository()
    private val ratingsRepository = FakeRatingsRepository()
    private val subscriptionManager = FakeSubscriptionManager()
    private val accountManager = FakeAccountManager()
    private val localizer = FakeLocalizer()
    private val formatterUtil = FakeFormatterUtil()
    private val navigator = FakeNavigator()
    private val dateTimeProvider = FakeDateTimeProvider(
        currentTime = Instant.fromEpochMilliseconds(epochMillis(2026, 8, 3)),
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()
        subscriptionManager.setAccess(SubscriptionFeature.Statistics, true)
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should show the empty state given no watch history`() = runTest(testDispatcher) {
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            val state = awaitItem()

            state.isLoading shouldBe false
            state.hasWatchHistory shouldBe false
            state.showEmptyState shouldBe true
            state.showContent shouldBe false
            state.totalWatchTime.shouldBeNull()
            state.mostWatchedShows.shouldBeEmpty()
        }
    }

    @Test
    fun `should show content given watch history`() = runTest(testDispatcher) {
        seedWatches()
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            val state = awaitItem()

            state.hasWatchHistory shouldBe true
            state.showContent shouldBe true
            state.showEmptyState shouldBe false
            val cells = state.heatMap.shouldNotBeNull().cells
            cells.first().date shouldBe "2026-01-01"
            cells.last().date shouldBe "2026-08-03"
        }
    }

    @Test
    fun `should report the total watch time given episodes carry a runtime`() = runTest(testDispatcher) {
        seedWatches(runtimeMinutes = 45)
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            val watchTime = awaitItem().totalWatchTime.shouldNotBeNull()

            watchTime.days shouldBe 0L
            watchTime.hours shouldBe 1L
            watchTime.minutes shouldBe 30L
        }
    }

    @Test
    fun `should omit the total watch time given no episode carries a runtime`() = runTest(testDispatcher) {
        seedWatches(runtimeMinutes = null)
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            awaitItem().totalWatchTime.shouldBeNull()
        }
    }

    @Test
    fun `should omit the rating tile given nothing is rated`() = runTest(testDispatcher) {
        seedWatches()
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            val tiles = awaitItem().tiles

            tiles.any { it.id == StatisticTileId.AverageRating } shouldBe false
            tiles.any { it.id == StatisticTileId.TitlesTracked } shouldBe true
        }
    }

    @Test
    fun `should add the rating tile given ratings exist`() = runTest(testDispatcher) {
        seedWatches()
        ratingsRepository.setUserRatingDistribution(mapOf(8 to 1L, 10 to 1L))
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            val tile = awaitItem().tiles.first { it.id == StatisticTileId.AverageRating }

            tile.value shouldBe "9.0"
        }
    }

    @Test
    fun `should order the tiles from the widest time span down`() = runTest(testDispatcher) {
        seedWatches()
        ratingsRepository.setUserRatingDistribution(mapOf(9 to 1L))
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()

            awaitItem().tiles.map { it.id } shouldBe listOf(
                StatisticTileId.PeakYear,
                StatisticTileId.WatchDaysThisYear,
                StatisticTileId.WatchDaysThisMonth,
                StatisticTileId.TopWeekday,
                StatisticTileId.LastThirtyDays,
                StatisticTileId.TitlesTracked,
                StatisticTileId.Episodes,
                StatisticTileId.AverageRating,
                StatisticTileId.WatchStreak,
                StatisticTileId.CurrentStreak,
            )
        }
    }

    @Test
    fun `should lock the screen given the subscription does not cover statistics`() = runTest(testDispatcher) {
        subscriptionManager.setAccess(SubscriptionFeature.Statistics, false)
        seedWatches()
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            val state = awaitItem()

            state.isLocked shouldBe true
            state.labels.lockedTitle shouldBe localizer.getString(StringResourceKey.LabelStatisticsLockedTitle)
            state.labels.lockedBadgeText shouldBe localizer.getString(StringResourceKey.LabelPremiumBadge)
        }
    }

    @Test
    fun `should note marked watched times given a Simkl account`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)
        seedWatches()
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            awaitItem().showsMarkedWatchedTimes shouldBe true
        }
    }

    @Test
    fun `should not note marked watched times given a Trakt account`() = runTest(testDispatcher) {
        seedWatches()
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            awaitItem().showsMarkedWatchedTimes shouldBe false
        }
    }

    @Test
    fun `should count only the watch statuses shows sit in`() = runTest(testDispatcher) {
        seedWatches()
        showWatchStatusRepository.setShowCountsByStatus(
            mapOf(WatchStatus.COMPLETED to 3L, WatchStatus.WATCHING to 1L),
        )
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            val breakdown = awaitItem().watchStatusBreakdown

            breakdown.size shouldBe 2
            breakdown.first { it.label == localizer.getString(StringResourceKey.LabelWatchStatusCompleted) }
                .showCount shouldBe 3L
        }
    }

    @Test
    fun `should open the show given a show is clicked`() = runTest(testDispatcher) {
        seedWatches()
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            awaitItem()

            presenter.dispatch(StatisticsAction.ShowClicked(showId = 1396))

            val route = navigator.lastNavigatedRoute.shouldNotBeNull()
            (route as ShowDetailsRoute).param.showId shouldBe 1396L
        }
    }

    @Test
    fun `should ignore a show click given the screen is locked`() = runTest(testDispatcher) {
        subscriptionManager.setAccess(SubscriptionFeature.Statistics, false)
        seedWatches()
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem()
            awaitItem().isLocked shouldBe true

            presenter.dispatch(StatisticsAction.ShowClicked(showId = 1396))

            navigator.navigatedRoutes.shouldBeEmpty()
        }
    }

    private fun seedWatches(runtimeMinutes: Long? = 45) {
        episodeRepository.setWatchedEpisodeRuntimes(
            listOf(
                WatchedEpisodeRuntime(watchedAt = epochMillis(2026, 8, 2), runtimeMinutes = runtimeMinutes),
                WatchedEpisodeRuntime(watchedAt = epochMillis(2026, 8, 3), runtimeMinutes = runtimeMinutes),
            ),
        )
        episodeRepository.setMostWatchedShows(
            listOf(
                MostWatchedShow(
                    showId = 1396,
                    title = "Breaking Bad",
                    posterPath = null,
                    episodeCount = 2,
                    totalRuntimeMinutes = (runtimeMinutes ?: 0) * 2,
                ),
            ),
        )
    }

    private fun buildPresenter(): StatisticsPresenter = StatisticsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        observeWatchStatisticsInteractor = ObserveWatchStatisticsInteractor(
            episodeRepository = episodeRepository,
            showWatchStatusRepository = showWatchStatusRepository,
            ratingsRepository = ratingsRepository,
            calculator = WatchStatisticsCalculator(dateTimeProvider),
        ),
        accountManager = accountManager,
        subscriptionManager = subscriptionManager,
        stateMapper = StatisticsStateMapper(
            localizer = localizer,
            formatterUtil = formatterUtil,
            dateTimeProvider = dateTimeProvider,
        ),
        navigator = navigator,
    )

    private companion object {
        fun epochMillis(year: Int, month: Int, day: Int): Long =
            LocalDateTime(year, month, day, 12, 0).toInstant(TimeZone.UTC).toEpochMilliseconds()
    }
}
