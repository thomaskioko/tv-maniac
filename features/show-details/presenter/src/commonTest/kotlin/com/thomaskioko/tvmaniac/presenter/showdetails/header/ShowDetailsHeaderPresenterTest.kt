package com.thomaskioko.tvmaniac.presenter.showdetails.header

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.root.nav.NotificationRationale
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncCalendarInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ObserveCommunityRatingInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ObserveRatingInteractor
import com.thomaskioko.tvmaniac.domain.ratings.RefreshCommunityRatingInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FollowShowInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.ObserveTraktListsInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.tvShowDetails
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetRoute
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.showlist.nav.ShowListRoute
import com.thomaskioko.tvmaniac.traktlists.api.TraktList
import com.thomaskioko.tvmaniac.traktlists.testing.FakeTraktListRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.collections.immutable.persistentListOf
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

internal class ShowDetailsHeaderPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val appCoroutineScope = CoroutineScope(testDispatcher + SupervisorJob())
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val watchProvidersRepository = FakeWatchProviderRepository()
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val ratingsRepository = FakeRatingsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val datastoreRepository = FakeDatastoreRepository()
    private val notificationManager = FakeNotificationManager()
    private val accountManager = FakeAccountManager()
    private val traktListRepository = FakeTraktListRepository()
    private val localizer = FakeLocalizer()
    private val formatterUtil = FakeFormatterUtil()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val navigator = FakeNavigator()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        notificationManager.reset()
        showDetailsRepository.setShowDetailsResult(tvShowDetails)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should map show details and localized status given details are available`() = runTest {
        showDetailsRepository.setShowDetailsResult(tvShowDetails)

        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.tmdbId shouldBe 849583L
            state.title shouldBe "Loki"
            state.year shouldBe "2021-06-09"
            state.language shouldBe "en"
            state.votes shouldBe 1L
            state.rating shouldBe 8.0
            state.isInLibrary shouldBe false
            state.genres shouldBe persistentListOf("Action", "Adventure", "Sci-Fi")
            state.status shouldBe localizer.getString(StringResourceKey.LabelLibraryStatusEnded)
        }
    }

    @Test
    fun `should add show to library given follow clicked and show is not in library`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ShowDetailsFollowClicked(isInLibrary = false))
        testDispatcher.scheduler.advanceUntilIdle()

        followedShowsRepository.addedShowIds shouldContainExactly listOf(SHOW_ID)
    }

    @Test
    fun `should remove show from library given follow clicked and show is in library`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ShowDetailsFollowClicked(isInLibrary = true))
        testDispatcher.scheduler.advanceUntilIdle()

        followedShowsRepository.removedShowIds shouldContainExactly listOf(SHOW_ID)
    }

    @Test
    fun `should expose canAddToList true given the active provider supports lists`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        val presenter = buildPresenter(supportsLists = true)

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().canAddToList shouldBe true
        }
    }

    @Test
    fun `should expose canAddToList false given the active provider does not support lists`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)

        val presenter = buildPresenter(supportsLists = false)

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().canAddToList shouldBe false
        }
    }

    @Test
    fun `should expose canAddToList true given no active provider`() = runTest {
        accountManager.setActiveProvider(null)

        val presenter = buildPresenter(supportsLists = false)

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().canAddToList shouldBe true
        }
    }

    @Test
    fun `should expose isInList true and listed label given show belongs to a list`() = runTest {
        traktListRepository.setListsForShow(
            listOf(traktList(isShowInList = true)),
        )

        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.isInList shouldBe true
            state.listActionLabel shouldBe localizer.getString(StringResourceKey.BtnInList)
        }
    }

    @Test
    fun `should expose isInList false and add label given show belongs to no list`() = runTest {
        traktListRepository.setListsForShow(
            listOf(traktList(isShowInList = false)),
        )

        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.isInList shouldBe false
            state.listActionLabel shouldBe localizer.getString(StringResourceKey.BtnAddToList)
        }
    }

    @Test
    fun `should refresh show details given auth state changes to logged in`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()
        showDetailsRepository.clearInvocations()

        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        testDispatcher.scheduler.advanceUntilIdle()

        val invocation = showDetailsRepository.fetchInvocations().last()
        invocation.id shouldBe SHOW_ID
        invocation.forceRefresh shouldBe true
    }

    @Test
    fun `should activate show list overlay given open show list dispatched and lists supported`() = runTest {
        accountManager.setActiveProvider(null)

        val presenter = buildPresenter(supportsLists = true)
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ShowDetailsOpenShowList)

        navigator.lastActivatedOverlay.shouldBeInstanceOf<ShowListRoute>()
    }

    @Test
    fun `should not activate show list overlay given open show list dispatched and lists unsupported`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)

        val presenter = buildPresenter(supportsLists = false)
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ShowDetailsOpenShowList)

        navigator.lastActivatedOverlay.shouldBeNull()
    }

    private fun buildPresenter(
        forceRefresh: Boolean = false,
        supportsLists: Boolean = true,
    ): ShowDetailsHeaderPresenter {
        val notificationRationale = object : NotificationRationale {
            override suspend fun showIfNeeded() = Unit
        }
        return ShowDetailsHeaderPresenter(
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            showId = SHOW_ID,
            forceRefresh = forceRefresh,
            navigator = navigator,
            notificationRationale = notificationRationale,
            followedShowsRepository = followedShowsRepository,
            followShowInteractor = FollowShowInteractor(
                followedShowsRepository = followedShowsRepository,
                libraryRepository = FakeLibraryRepository(),
                syncShowMetadataInteractor = SyncShowMetadataInteractor(
                    showDetailsRepository = showDetailsRepository,
                    seasonDetailsRepository = seasonDetailsRepository,
                    watchProviderRepository = watchProvidersRepository,
                    dispatchers = dispatchers,
                ),
                appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
            ),
            showDetailsInteractor = ShowDetailsInteractor(
                showDetailsRepository = showDetailsRepository,
                dispatchers = dispatchers,
            ),
            observableShowDetailsInteractor = ObservableShowDetailsInteractor(
                showDetailsRepository = showDetailsRepository,
                formatterUtil = formatterUtil,
                dispatchers = dispatchers,
            ),
            refreshCommunityRatingInteractor = RefreshCommunityRatingInteractor(ratingsRepository),
            observeRatingInteractor = ObserveRatingInteractor(ratingsRepository),
            observeCommunityRatingInteractor = ObserveCommunityRatingInteractor(ratingsRepository),
            observeTraktListsInteractor = ObserveTraktListsInteractor(traktListRepository),
            syncCalendarInteractor = SyncCalendarInteractor(
                episodeRepository = episodeRepository,
                dateTimeProvider = dateTimeProvider,
                activeProviderFeatures = { FakeProviderFeatures(supportsCalendar = true) },
                logger = FakeLogger(),
                dispatchers = dispatchers,
            ),
            scheduleEpisodeNotificationsInteractor = ScheduleEpisodeNotificationsInteractor(
                datastoreRepository = datastoreRepository,
                episodeRepository = episodeRepository,
                notificationManager = notificationManager,
                localizer = localizer,
                dateTimeProvider = dateTimeProvider,
                logger = FakeLogger(),
                dispatchers = dispatchers,
            ),
            notificationManager = notificationManager,
            accountManager = accountManager,
            activeProviderFeatures = { FakeProviderFeatures(supportsLists = supportsLists) },
            localizer = localizer,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )
    }

    @Test
    fun `should emit user and community rating given rating is observed`() = runTest {
        ratingsRepository.setShowRating(
            ShowRating(userRating = 9, communityRating = 8.4, communityVotes = 500, pendingAction = PendingAction.NOTHING),
        )

        val presenter = buildPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.userRating shouldBe 9
            state.communityRating shouldBe 8.4
            state.communityVotes shouldBe 500L
        }
    }

    @Test
    fun `should navigate to rating sheet given show rating clicked`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ShowRatingClicked)

        val route = navigator.lastActivatedOverlay.shouldBeInstanceOf<RatingSheetRoute>()
        route.param.ratingType shouldBe RatingEntityType.SHOW
        route.param.id shouldBe SHOW_ID
    }

    private companion object {
        private const val SHOW_ID = 84958L

        private fun traktList(isShowInList: Boolean): TraktList = TraktList(
            id = 1L,
            slug = "watchlist",
            name = "Watchlist",
            description = null,
            itemCount = if (isShowInList) 1L else 0L,
            isShowInList = isShowInList,
        )
    }
}
