package com.thomaskioko.tvmaniac.lists.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.domain.lists.ObserveUserListsInteractor
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.lists.api.UserListEntity
import com.thomaskioko.tvmaniac.lists.presenter.model.UserListItem
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.navigation.testing.TestNavigator
import com.thomaskioko.tvmaniac.navigation.testing.test
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class ListsPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val listRepository = FakeListRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should start loading with the page labels`() = runTest {
        createPresenter().state.test {
            val initial = awaitItem()

            initial.isLoading shouldBe true
            initial.title shouldBe "Lists"
            initial.emptyMessage shouldBe "You don't have any lists yet."
            initial.lists.shouldBeEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should show every list including empty ones given lists exist`() = runTest {
        listRepository.setLists(
            listOf(
                createListEntity(id = 1, name = "Favorites", itemCount = 12, posterPaths = listOf("/list-poster.jpg")),
                createListEntity(id = 2, name = "Comfort watches", itemCount = 0),
            ),
        )

        createPresenter().state.test {
            awaitItem().isLoading shouldBe true

            val loaded = awaitItem()
            loaded.isLoading shouldBe false
            loaded.lists shouldBe persistentListOf(
                UserListItem(
                    id = 1,
                    name = "Favorites",
                    itemCount = 12,
                    itemCountLabel = "12 shows",
                    posterUrls = persistentListOf("/list-poster.jpg"),
                ),
                UserListItem(
                    id = 2,
                    name = "Comfort watches",
                    itemCount = 0,
                    itemCountLabel = "0 shows",
                    posterUrls = persistentListOf(),
                ),
            )
        }
    }

    @Test
    fun `should show no lists given the user has none`() = runTest {
        createPresenter().state.test {
            awaitItem().isLoading shouldBe true

            val loaded = awaitItem()
            loaded.isLoading shouldBe false
            loaded.lists.shouldBeEmpty()
            loaded.errorMessage shouldBe null
        }
    }

    @Test
    fun `should surface the error given the lists stream fails`() = runTest {
        listRepository.setObserveError(IllegalStateException("Database unavailable"))

        createPresenter().state.test {
            awaitItem().isLoading shouldBe true

            val failed = awaitItem()
            failed.isLoading shouldBe false
            failed.errorMessage shouldBe "Database unavailable"
            failed.lists.shouldBeEmpty()
        }
    }

    @Test
    fun `should navigate back given back is clicked`() = runTest {
        val navigator = TestNavigator()
        val presenter = createPresenter(navigator = navigator)

        navigator.test {
            presenter.dispatch(ListsAction.BackClicked)
            awaitNavigateBack()
        }
    }

    private fun createPresenter(navigator: Navigator = NoOpNavigator()): ListsPresenter = ListsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        navigator = navigator,
        localizer = FakeLocalizer(),
        errorToStringMapper = { it.message ?: "Test error" },
        logger = FakeLogger(),
        observeUserListsInteractor = ObserveUserListsInteractor(listRepository),
    )

    private fun createListEntity(
        id: Long,
        name: String,
        itemCount: Long,
        posterPaths: List<String> = emptyList(),
    ): UserListEntity = UserListEntity(
        id = id,
        slug = name.lowercase().replace(' ', '-'),
        name = name,
        description = null,
        itemCount = itemCount,
        createdAt = "2024-01-01",
        posterPaths = posterPaths,
    )
}
