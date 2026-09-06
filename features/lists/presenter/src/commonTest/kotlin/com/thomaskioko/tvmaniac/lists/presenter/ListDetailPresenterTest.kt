package com.thomaskioko.tvmaniac.lists.presenter

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.domain.lists.FetchMissingListShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.lists.ToggleShowInListInteractor
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.lists.api.ListShowItem
import com.thomaskioko.tvmaniac.lists.nav.model.ListDetailParam
import com.thomaskioko.tvmaniac.lists.presenter.model.ListShow
import com.thomaskioko.tvmaniac.lists.presenter.model.RemoveConfirmation
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.navigation.testing.TestNavigator
import com.thomaskioko.tvmaniac.navigation.testing.test
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class ListDetailPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val listRepository = FakeListRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should start with the list name and no shows`() = runTest {
        createPresenter().state.test {
            val initial = awaitItem()

            initial.title shouldBe LIST_NAME
            initial.emptyMessage shouldBe "No shows in this list yet."
            initial.items.shouldBeEmpty()
            initial.removeConfirmation shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should expose the page in the order the repository returns it`() = runTest {
        listRepository.setPagedListShows(PagingData.from(listOf(breakingBad(), betterCallSaul())))

        createPresenter().state.test {
            val loaded = awaitUntil { it.items.isNotEmpty() }

            loaded.items shouldBe listOf(
                ListShow(tmdbId = BREAKING_BAD_ID, title = "Breaking Bad", posterUrl = "/breaking-bad.jpg"),
                ListShow(tmdbId = BETTER_CALL_SAUL_ID, title = "Better Call Saul", posterUrl = null),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should surface the mapped error given the first page fails`() = runTest {
        listRepository.setPagedListShows(
            PagingData.from(
                data = emptyList(),
                sourceLoadStates = LoadStates(
                    refresh = LoadState.Error(RuntimeException("boom")),
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.NotLoading(endOfPaginationReached = true),
                ),
            ),
        )

        createPresenter().state.test {
            val failed = awaitUntil { it.errorMessage != null }

            failed.errorMessage shouldBe "mapped:boom"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should clear the error message given it is dismissed`() = runTest {
        listRepository.setPagedListShows(
            PagingData.from(
                data = emptyList(),
                sourceLoadStates = LoadStates(
                    refresh = LoadState.Error(RuntimeException("boom")),
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.NotLoading(endOfPaginationReached = true),
                ),
            ),
        )
        val presenter = createPresenter()

        presenter.state.test {
            awaitUntil { it.errorMessage != null }

            presenter.dispatch(ListDetailAction.DismissErrorMessage)

            awaitItem().errorMessage shouldBe null
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should surface the append error given the next page fails`() = runTest {
        listRepository.setPagedListShows(
            PagingData.from(
                data = listOf(breakingBad()),
                sourceLoadStates = LoadStates(
                    refresh = LoadState.NotLoading(endOfPaginationReached = false),
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.Error(RuntimeException("offline")),
                ),
            ),
        )

        createPresenter().state.test {
            val failed = awaitUntil { it.appendError != null }

            failed.appendError shouldBe "mapped:offline"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to show details given a show is clicked`() = runTest {
        val navigator = TestNavigator()
        val presenter = createPresenter(navigator = navigator)

        navigator.test {
            presenter.dispatch(ListDetailAction.ShowClicked(BREAKING_BAD_ID))
            awaitNavigateTo(ShowDetailsRoute(ShowDetailsParam(showId = BREAKING_BAD_ID)))
        }
    }

    @Test
    fun `should ask for confirmation naming the show and the list given remove is requested`() = runTest {
        listRepository.setPagedListShows(PagingData.from(listOf(breakingBad())))
        val presenter = createPresenter()

        presenter.state.test {
            awaitUntil { it.items.isNotEmpty() }

            presenter.dispatch(ListDetailAction.RemoveRequested(BREAKING_BAD_ID))

            awaitItem().removeConfirmation shouldBe RemoveConfirmation(
                tmdbId = BREAKING_BAD_ID,
                title = "Remove from list?",
                message = "Remove Breaking Bad from Comfort watches?",
                confirmLabel = "Remove",
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should toggle the show out of the list and clear the confirmation given remove is confirmed`() = runTest {
        listRepository.setPagedListShows(PagingData.from(listOf(breakingBad())))
        val presenter = createPresenter()

        presenter.state.test {
            awaitUntil { it.items.isNotEmpty() }
            presenter.dispatch(ListDetailAction.RemoveRequested(BREAKING_BAD_ID))
            awaitUntil { it.removeConfirmation != null }

            presenter.dispatch(ListDetailAction.RemoveConfirmed)

            awaitItem().removeConfirmation shouldBe null
            advanceUntilIdle()
            listRepository.toggledShows() shouldBe listOf(LIST_ID to BREAKING_BAD_ID)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should surface the mapped error and keep it through a page refresh given the removal fails`() = runTest {
        listRepository.setPagedListShows(PagingData.from(listOf(breakingBad())))
        listRepository.setToggleError(RuntimeException("offline"))
        val presenter = createPresenter()

        presenter.state.test {
            awaitUntil { it.items.isNotEmpty() }
            presenter.dispatch(ListDetailAction.RemoveRequested(BREAKING_BAD_ID))
            awaitUntil { it.removeConfirmation != null }

            presenter.dispatch(ListDetailAction.RemoveConfirmed)

            val failed = awaitUntil { it.errorMessage != null }
            failed.errorMessage shouldBe "mapped:offline"

            listRepository.setPagedListShows(PagingData.from(listOf(breakingBad(), betterCallSaul())))
            val refreshed = awaitUntil { it.items.size == 2 }
            refreshed.errorMessage shouldBe "mapped:offline"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should clear the confirmation and toggle nothing given remove is dismissed`() = runTest {
        listRepository.setPagedListShows(PagingData.from(listOf(breakingBad())))
        val presenter = createPresenter()

        presenter.state.test {
            awaitUntil { it.items.isNotEmpty() }
            presenter.dispatch(ListDetailAction.RemoveRequested(BREAKING_BAD_ID))
            awaitUntil { it.removeConfirmation != null }

            presenter.dispatch(ListDetailAction.RemoveDismissed)

            awaitItem().removeConfirmation shouldBe null
            advanceUntilIdle()
            listRepository.toggledShows().shouldBeEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should fetch details once for every show without a poster given the page opens`() = runTest {
        listRepository.setTmdbIdsMissingPoster(listOf(BETTER_CALL_SAUL_ID))

        createPresenter()
        advanceUntilIdle()

        showDetailsRepository.fetchInvocations() shouldBe listOf(
            FakeShowDetailsRepository.FetchInvocation(id = BETTER_CALL_SAUL_ID, forceRefresh = false),
        )
    }

    @Test
    fun `should navigate back given back is clicked`() = runTest {
        val navigator = TestNavigator()
        val presenter = createPresenter(navigator = navigator)

        navigator.test {
            presenter.dispatch(ListDetailAction.BackClicked)
            awaitNavigateBack()
        }
    }

    private suspend fun app.cash.turbine.ReceiveTurbine<ListDetailState>.awaitUntil(
        predicate: (ListDetailState) -> Boolean,
    ): ListDetailState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }

    private fun createPresenter(navigator: Navigator = NoOpNavigator()): ListDetailPresenter {
        val lifecycle = LifecycleRegistry()
        return ListDetailPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            param = ListDetailParam(listId = LIST_ID, name = LIST_NAME),
            navigator = navigator,
            localizer = FakeLocalizer(),
            listRepository = listRepository,
            toggleShowInListInteractor = ToggleShowInListInteractor(
                repository = listRepository,
                userRepository = FakeUserRepository(),
                activeProviderFeatures = { FakeProviderFeatures(supportsLists = false) },
            ),
            fetchMissingListShowDetailsInteractor = FetchMissingListShowDetailsInteractor(
                listRepository = listRepository,
                showDetailsRepository = showDetailsRepository,
                dispatchers = dispatchers,
            ),
            errorToStringMapper = { "mapped:${it.message}" },
            logger = FakeLogger(),
        ).also { lifecycle.resume() }
    }

    private fun breakingBad(): ListShowItem = ListShowItem(
        tmdbId = BREAKING_BAD_ID,
        name = "Breaking Bad",
        posterPath = "/breaking-bad.jpg",
        year = "2008",
    )

    private fun betterCallSaul(): ListShowItem = ListShowItem(
        tmdbId = BETTER_CALL_SAUL_ID,
        name = "Better Call Saul",
        posterPath = null,
        year = "2015",
    )

    private companion object {
        private const val LIST_ID = 7L
        private const val LIST_NAME = "Comfort watches"
        private const val BREAKING_BAD_ID = 1396L
        private const val BETTER_CALL_SAUL_ID = 60059L
    }
}
