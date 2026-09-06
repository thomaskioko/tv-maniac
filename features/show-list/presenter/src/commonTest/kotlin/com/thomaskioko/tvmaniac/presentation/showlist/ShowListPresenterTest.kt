package com.thomaskioko.tvmaniac.presentation.showlist

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.domain.lists.CreateListInteractor
import com.thomaskioko.tvmaniac.domain.lists.ObserveListsForShowInteractor
import com.thomaskioko.tvmaniac.domain.lists.SyncListsInteractor
import com.thomaskioko.tvmaniac.domain.lists.ToggleShowInListInteractor
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.lists.api.UserList
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.showlist.nav.ShowListParam
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
    private val listRepository = FakeListRepository()
    private val accountManager = FakeAccountManager()
    private var providerFeatures: ProviderFeatures = FakeProviderFeatures(supportsLists = true)
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
        initial.labels.emptyListText.isNotEmpty() shouldBe true
        initial.labels.listsHeaderText.isNotEmpty() shouldBe true
        initial.labels.createListButtonText.isNotEmpty() shouldBe true
        initial.labels.createListDoneText.isNotEmpty() shouldBe true
        initial.labels.createListPlaceholder.isNotEmpty() shouldBe true
    }

    @Test
    fun `should clear loading flag once combine emits`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.isLoading shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit lists given no account is connected`() = runTest {
        listRepository.setListsForShow(listOf(userList(id = 1L)))

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()

            state.lists shouldHaveSize 1
            state.lists[0].id shouldBe 1L
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit lists and open the create field given a Simkl session`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)
        providerFeatures = FakeProviderFeatures(supportsLists = false)
        listRepository.setListsForShow(listOf(userList(id = 1L)))

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().lists shouldHaveSize 1

            presenter.dispatch(ShowListAction.ShowCreateListField)
            testDispatcher.scheduler.advanceUntilIdle()

            expectMostRecentItem().showCreateListField shouldBe true
            listRepository.fetchUserListsInvocations shouldBe 0
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit lists given user is logged in and lists exist`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        listRepository.setListsForShow(
            listOf(
                UserList(
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

            state.lists shouldHaveSize 1
            state.lists[0].id shouldBe 1L
            state.lists[0].name shouldBe "Watchlist"
            state.lists[0].isShowInList shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit correct show counts given lists are synced`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        listRepository.setListsForShow(
            listOf(
                UserList(
                    id = 1L,
                    slug = "watchlist",
                    name = "Watchlist",
                    description = null,
                    itemCount = 10L,
                    isShowInList = false,
                ),
                UserList(
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

            state.lists shouldHaveSize 2
            state.lists[0].showCountText shouldBe "10 shows"
            state.lists[1].showCountText shouldBe "3 shows"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should sync lists given user becomes logged in`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            accountManager.setActiveProvider(SyncProviderSource.TRAKT)
            testDispatcher.scheduler.advanceUntilIdle()

            listRepository.fetchUserListsInvocations shouldBe 1
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
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
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
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
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
    fun `should invoke toggle interactor given ToggleShowInList is dispatched with no account`() = runTest {
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(
                ShowListAction.ToggleShowInList(listId = 7L, isCurrentlyInList = false),
            )
            testDispatcher.scheduler.advanceUntilIdle()

            listRepository.toggledShows() shouldHaveSize 1
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should invoke toggle interactor given ToggleShowInList is dispatched`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
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
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        listRepository.setListsForShow(
            listOf(
                UserList(
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
        listRepository.setToggleGate(gate)

        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            presenter.dispatch(
                ShowListAction.ToggleShowInList(listId = 7L, isCurrentlyInList = false),
            )
            testDispatcher.scheduler.runCurrent()

            val whileToggling = expectMostRecentItem()
            whileToggling.lists[0].isToggling shouldBe true

            gate.complete(Unit)
            testDispatcher.scheduler.advanceUntilIdle()

            val afterToggling = expectMostRecentItem()
            afterToggling.lists[0].isToggling shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should ignore duplicate ToggleShowInList given a toggle is already in flight`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        listRepository.setListsForShow(
            listOf(
                UserList(
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
        listRepository.setToggleGate(gate)

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

            listRepository.toggleShowInListInvocations shouldBe 1

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

    private fun createPresenter(
        showId: Long = 100L,
        appScopeLauncher: FakeAppScopeLauncher = FakeAppScopeLauncher(appCoroutineScope),
    ): ShowListPresenter = ShowListPresenter(
        param = ShowListParam(showId = showId),
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        observeListsForShowInteractor = ObserveListsForShowInteractor(listRepository),
        navigator = navigator,
        accountManager = accountManager,
        syncListsInteractor = SyncListsInteractor(listRepository, userRepository) { providerFeatures },
        createListInteractor = CreateListInteractor(listRepository, userRepository) { providerFeatures },
        toggleShowInListInteractor = ToggleShowInListInteractor(listRepository, userRepository) { providerFeatures },
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        mapper = ShowListMapper(localizer),
        logger = logger,
        appScopeLauncher = appScopeLauncher,
    )

    private fun userList(id: Long): UserList = UserList(
        id = id,
        slug = "list-$id",
        name = "List $id",
        description = null,
        itemCount = 0L,
        isShowInList = false,
    )
}
