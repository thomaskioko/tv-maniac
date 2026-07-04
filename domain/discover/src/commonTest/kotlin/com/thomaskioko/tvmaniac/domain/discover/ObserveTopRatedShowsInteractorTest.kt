package com.thomaskioko.tvmaniac.domain.discover

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObserveTopRatedShowsInteractorTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeTopRatedShowsRepository()
    private lateinit var interactor: ObserveTopRatedShowsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveTopRatedShowsInteractor(repository = repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return empty list given no shows in repository`() = runTest {
        repository.setTopRatedShows(emptyList())
        interactor(Unit)

        interactor.flow.test {
            awaitItem() shouldBe emptyList()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return shows given repository has top rated shows`() = runTest {
        val shows = createTestShows()
        repository.setTopRatedShows(shows)
        interactor(Unit)

        interactor.flow.test {
            awaitItem() shouldBe shows
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun createTestShows() = List(3) { index ->
        ShowEntity(
            showId = index.toLong(),
            tmdbId = index.toLong(),
            title = "Show $index",
            posterPath = "poster_$index.jpg",
            inLibrary = false,
            overview = "Overview $index",
        )
    }
}
