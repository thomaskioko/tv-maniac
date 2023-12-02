package com.thomaskioko.tvmaniac.presentation.discover

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.shows.testing.FakeDiscoverRepository
import com.thomaskioko.tvmaniac.tmdb.testing.FakeShowImagesRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
@OptIn(ExperimentalCoroutinesApi::class)
internal class DiscoverShowsPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val discoverRepository = FakeDiscoverRepository()
    private val imagesRepository = FakeShowImagesRepository()

    private lateinit var presenter: DiscoverShowsPresenter

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        /*  presenter = DiscoverPresenter(discoverRepository, imagesRepository)*/
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given an result is loaded then correct state is emitted`() = runTest {
        discoverRepository.setShowCategory(categoryResult(1))
        discoverRepository.setShowCategory(categoryResult(2))
        discoverRepository.setShowCategory(categoryResult(3))
        discoverRepository.setShowCategory(categoryResult(4))

        presenter.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe discoverContent
        }
    }
}
