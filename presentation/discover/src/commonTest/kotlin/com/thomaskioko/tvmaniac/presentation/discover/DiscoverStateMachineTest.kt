package com.thomaskioko.tvmaniac.presentation.discover

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.shows.testing.FakeDiscoverRepository
import com.thomaskioko.tvmaniac.tmdb.testing.FakeShowImagesRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DiscoverStateMachineTest {

    private val discoverRepository = FakeDiscoverRepository()
    private val imagesRepository = FakeShowImagesRepository()
    private val stateMachine = DiscoverStateMachine(discoverRepository, imagesRepository)

    @Test
    fun `given an result is loaded then correct state is emitted`() = runTest {
        discoverRepository.setShowCategory(categoryResult(1))
        discoverRepository.setShowCategory(categoryResult(2))
        discoverRepository.setShowCategory(categoryResult(3))
        discoverRepository.setShowCategory(categoryResult(4))

        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe discoverContent
        }
    }
}
