package com.thomaskioko.tvmaniac.presentation.showlist

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAuthManager
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.domain.traktlists.CreateTraktListInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.ObserveTraktListsInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.SyncTraktListsInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.ToggleShowInListInteractor
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlag
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.showlist.nav.ShowListParam
import com.thomaskioko.tvmaniac.traktlists.api.TraktList
import com.thomaskioko.tvmaniac.traktlists.testing.FakeTraktListRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
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

internal class ShowListPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val appCoroutineScope = CoroutineScope(testDispatcher + SupervisorJob())
    private val traktListRepository = FakeTraktListRepository()
    private val accountManager = FakeAccountManager()
    private val authManager = FakeAuthManager()
    private val simklAuthManager = FakeAuthManager(AccountProvider.SIMKL)
    private val simklFlag = FakeFeatureFlag(initial = false)
    private val userRepository = FakeUserRepository()
    private val localizer = FakeLocalizer()
    private val logger = FakeLogger()
    private val navigator = FakeNavigator()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        navigator.reset()
    }

    @Test
    fun `should expose resolved copy and loading flag in initial state given presenter is created`() {
        val presenter = createPresenter()

        val initial = presenter.state.value

        initial.isLoading shouldBe true
        initial.labels.sheetTitle.isNotEmpty() shouldBe true
        initial.labels.loginRequiredTitle.isNotEmpty() shouldBe true
        initial.labels.loginRequiredMessage.isNotEmpty() shouldBe true
        initial.labels.emptyListText.isNotEmpty() shouldBe true
        initial.labels.listsHeaderText.isNotEmpty() shouldBe true
        initial.labels.createListButtonText.isNotEmpty() shouldBe true
        initial.labels.createListDoneText.isNotEmpty() shouldBe true
        initial.labels.createListPlaceholder.isNotEmpty() shouldBe true
    }

    @Test
    fun `should clear loading flag once combine emits`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.isLoading shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit logged-out state given user is not logged in`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.isLoggedIn shouldBe false
            state.traktLists shouldHaveSize 0
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit lists given user is logged in and lists exist`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        traktListRepository.setListsForShow(
            listOf(
                TraktList(
                    id = 1L,
                    slug = "watchlist",
                    name = "Watchlist",
                    description = "My watchlist",
                    itemCount = 5L,
                    isShowInList = true,
                ),
            ),
        )

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.isLoggedIn shouldBe true
            state.traktLists shouldHaveSize 1
            state.traktLists[0].id shouldBe 1L
            state.traktLists[0].name shouldBe "Watchlist"
            state.traktLists[0].isShowInList shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit correct show counts given lists are synced`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        traktListRepository.setListsForShow(
            listOf(
                TraktList(
                    id = 1L,
                    slug = "watchlist",
                    name = "Watchlist",
                    description = null,
                    itemCount = 10L,
                    isShowInList = false,
                ),
                TraktList(
                    id = 2L,
                    slug = "favorites",
                    name = "Favorites",
                    description = null,
                    itemCount = 3L,
                    isShowInList = true,
                ),
            ),
        )

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.traktLists shouldHaveSize 2
            state.traktLists[0].showCountText shouldBe "10 shows"
            state.traktLists[1].showCountText shouldBe "3 shows"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should sync lists given user becomes logged in`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            accountManager.setActiveProvider(AccountProvider.TRAKT)
            testDispatcher.scheduler.advanceUntilIdle()

            traktListRepository.fetchUserListsInvocations shouldBe 1
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should launch web view given Login is dispatched`() = runTest {
        var launchCount = 0
        authManager.setOnLaunchWebView { launchCount += 1 }

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.Login(AccountProvider.TRAKT))

            launchCount shouldBe 1
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should launch the chosen provider given a non default provider`() = runTest {
        var traktLaunches = 0
        var simklLaunches = 0
        authManager.setOnLaunchWebView { traktLaunches += 1 }
        simklAuthManager.setOnLaunchWebView { simklLaunches += 1 }
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.Login(AccountProvider.SIMKL))

            simklLaunches shouldBe 1
            traktLaunches shouldBe 0
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should open create list field given ShowCreateListField is dispatched`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.ShowCreateListField)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.showCreateListField shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should close field and clear name given DismissCreateListField is dispatched`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.ShowCreateListField)
            presenter.dispatch(ShowListAction.UpdateCreateListName("New List"))
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.DismissCreateListField)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.showCreateListField shouldBe false
            state.createListName shouldBe ""
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update name given UpdateCreateListName is dispatched`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.UpdateCreateListName("Action Movies"))
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.createListName shouldBe "Action Movies"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should close field and clear name given CreateListSubmitted succeeds`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.ShowCreateListField)
            presenter.dispatch(ShowListAction.UpdateCreateListName("Action Movies"))
            presenter.dispatch(ShowListAction.CreateListSubmitted)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.showCreateListField shouldBe false
            state.createListName shouldBe ""
            state.isCreatingList shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should surface error message given CreateListSubmitted is dispatched with blank name`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.ShowCreateListField)
            presenter.dispatch(ShowListAction.CreateListSubmitted)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.message.shouldNotBeNull()
            state.isCreatingList shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should not crash given ToggleShowInList is dispatched while logged out`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(
                ShowListAction.ToggleShowInList(listId = 7L, isCurrentlyInList = false),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should invoke toggle interactor given ToggleShowInList is dispatched`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(
                ShowListAction.ToggleShowInList(
                    listId = 7L,
                    isCurrentlyInList = false,
                ),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should mark list as toggling while interactor is running`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        traktListRepository.setListsForShow(
            listOf(
                TraktList(
                    id = 7L,
                    slug = "favorites",
                    name = "Favorites",
                    description = null,
                    itemCount = 1L,
                    isShowInList = false,
                ),
            ),
        )
        val gate = CompletableDeferred<Unit>()
        traktListRepository.setToggleGate(gate)

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(
                ShowListAction.ToggleShowInList(listId = 7L, isCurrentlyInList = false),
            )
            testDispatcher.scheduler.runCurrent()

            val whileToggling = expectMostRecentItem()
            whileToggling.traktLists[0].isToggling shouldBe true

            gate.complete(Unit)
            testDispatcher.scheduler.advanceUntilIdle()

            val afterToggling = expectMostRecentItem()
            afterToggling.traktLists[0].isToggling shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should ignore duplicate ToggleShowInList given a toggle is already in flight`() = runTest {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        traktListRepository.setListsForShow(
            listOf(
                TraktList(
                    id = 7L,
                    slug = "favorites",
                    name = "Favorites",
                    description = null,
                    itemCount = 1L,
                    isShowInList = false,
                ),
            ),
        )
        val gate = CompletableDeferred<Unit>()
        traktListRepository.setToggleGate(gate)

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(
                ShowListAction.ToggleShowInList(listId = 7L, isCurrentlyInList = false),
            )
            testDispatcher.scheduler.runCurrent()

            presenter.dispatch(
                ShowListAction.ToggleShowInList(listId = 7L, isCurrentlyInList = false),
            )
            testDispatcher.scheduler.runCurrent()

            traktListRepository.toggleShowInListInvocations shouldBe 1

            gate.complete(Unit)
            testDispatcher.scheduler.advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should dismiss overlay given Dismiss is dispatched`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.Dismiss)

            navigator.overlayDismissCount shouldBe 1
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should clear message given MessageShown is dispatched`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(ShowListAction.MessageShown(id = 42L))
            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should expose only the trakt option given the simkl flag is off`() = runTest {
        val presenter = createPresenter()
        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().authProviders.map { it.provider } shouldBe listOf(AccountProvider.TRAKT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should expose both provider options given the simkl flag is on`() = runTest {
        simklFlag.value = true
        val presenter = createPresenter()
        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().authProviders.map { it.provider } shouldBe
                listOf(AccountProvider.TRAKT, AccountProvider.SIMKL)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createPresenter(
        showId: Long = 100L,
        appScopeLauncher: FakeAppScopeLauncher = FakeAppScopeLauncher(appCoroutineScope),
    ): ShowListPresenter = ShowListPresenter(
        param = ShowListParam(showId = showId),
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        observeTraktListsInteractor = ObserveTraktListsInteractor(traktListRepository),
        navigator = navigator,
        accountManager = accountManager,
        authManagers = mapOf(AccountProvider.TRAKT to authManager, AccountProvider.SIMKL to simklAuthManager),
        simklLoginFlag = simklFlag,
        syncTraktListsInteractor = SyncTraktListsInteractor(traktListRepository, userRepository),
        createTraktListInteractor = CreateTraktListInteractor(traktListRepository, userRepository),
        toggleShowInListInteractor = ToggleShowInListInteractor(traktListRepository, userRepository),
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        mapper = ShowListMapper(localizer),
        logger = logger,
        appScopeLauncher = appScopeLauncher,
    )
}
