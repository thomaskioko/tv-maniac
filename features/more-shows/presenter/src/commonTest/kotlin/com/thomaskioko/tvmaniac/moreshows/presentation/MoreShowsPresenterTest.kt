package com.thomaskioko.tvmaniac.moreshows.presentation

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.shows.api.model.Category.POPULAR
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class MoreShowsPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val popularShowsRepository = FakePopularShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should expose mapped error message given refresh fails`() = runTest {
        popularShowsRepository.setPagedShows(
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

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
    ): MoreShowsPresenter = MoreShowsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        categoryId = POPULAR.id,
        navigator = NoOpNavigator(),
        popularShowsRepository = popularShowsRepository,
        upcomingShowsRepository = upcomingShowsRepository,
        trendingShowsRepository = trendingShowsRepository,
        topRatedShowsRepository = topRatedShowsRepository,
        errorToStringMapper = ErrorToStringMapper { "mapped:${it.message}" },
    ).also { lifecycle.resume() }
}
