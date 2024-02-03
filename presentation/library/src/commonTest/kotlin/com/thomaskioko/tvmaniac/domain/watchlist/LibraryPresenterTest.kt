package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryContent
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.LoadingShows
import com.thomaskioko.tvmaniac.presentation.watchlist.model.LibraryItem
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.watchlist.testing.FakeLibraryRepository
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@Suppress("TestFunctionName")
@OptIn(ExperimentalCoroutinesApi::class)
class LibraryPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val repository = FakeLibraryRepository()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var presenter: LibraryPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)

        lifecycle.resume()
        presenter = LibraryPresenter(
            navigateToShowDetails = {},
            repository = repository,
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun GIVEN_no_data_is_cached_VERIFY_correct_state_is_emitted() = runTest {
        repository.setFollowedResult(emptyList())
        presenter.state.test {
            awaitItem() shouldBe LoadingShows
            awaitItem() shouldBe LibraryContent(persistentListOf())
        }
    }

    @Test
    fun GIVEN_data_is_cached_VERIFY_correct_state_is_emitted() = runTest {
        repository.setFollowedResult(cachedResult)

        presenter.state.test {
            awaitItem() shouldBe LoadingShows
            awaitItem() shouldBe LibraryContent(list = uiResult)
        }
    }

    @Test
    fun GIVEN_data_is_cached_and_updated_VERIFY_correct_state_is_emitted() = runTest {
        repository.setFollowedResult(cachedResult)

        presenter.state.test {
            awaitItem() shouldBe LoadingShows
            awaitItem() shouldBe LibraryContent(list = uiResult)

            repository.setObserveResult(successResult)
            awaitItem() shouldBe LibraryContent(list = expectedUiResult())
        }
    }

    private val successResult = Either.Right(
        listOf(
            LibraryShows(
                id = Id(84958),
                name = "Loki",
                poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                created_at = 12345645,
            ),
            LibraryShows(
                id = Id(1232),
                name = "The Lazarus Project",
                poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                created_at = 12345645,
            ),
        ),
    )

    private fun expectedUiResult() = successResult.right.map {
        LibraryItem(
            tmdbId = it.id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
        )
    }.toPersistentList()
}
