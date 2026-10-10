package com.thomaskioko.tvmaniac.genreshows.presentation

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.genreshows.nav.model.GenreShowsParam
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.navigation.testing.TestNavigator
import com.thomaskioko.tvmaniac.navigation.testing.test
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class GenreShowsPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val genreRepository = FakeGenreRepository()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should title the screen with the genre name given the route param`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            awaitItem().title shouldBe "Drama"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should expose mapped error message given refresh fails`() = runTest {
        genreRepository.setPagedGenreShows(
            PagingData.from(
                data = emptyList(),
                sourceLoadStates = LoadStates(
                    refresh = LoadState.Error(RuntimeException("boom")),
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.NotLoading(endOfPaginationReached = true),
                ),
            ),
        )
        val presenter = buildPresenter()

        presenter.state.test {
            var state = awaitItem()
            while (state.errorMessage == null) {
                state = awaitItem()
            }

            state.errorMessage shouldBe "mapped:boom"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to show details given a show is clicked`() = runTest {
        val testNavigator = TestNavigator()
        val presenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            presenter.dispatch(GenreShowClicked(showId = 42L))

            awaitNavigateTo(ShowDetailsRoute(ShowDetailsParam(showId = 42L)))
        }
    }

    @Test
    fun `should navigate back given back is clicked`() = runTest {
        val testNavigator = TestNavigator()
        val presenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            presenter.dispatch(GenreShowsBackClicked)

            awaitNavigateBack()
        }
    }

    private fun buildPresenter(
        navigator: Navigator = NoOpNavigator(),
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
    ): GenreShowsPresenter = GenreShowsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        param = GenreShowsParam(slug = "drama", name = "Drama", category = GenreShowCategory.POPULAR),
        navigator = navigator,
        genreRepository = genreRepository,
        errorToStringMapper = ErrorToStringMapper { "mapped:${it.message}" },
    ).also { lifecycle.resume() }
}
