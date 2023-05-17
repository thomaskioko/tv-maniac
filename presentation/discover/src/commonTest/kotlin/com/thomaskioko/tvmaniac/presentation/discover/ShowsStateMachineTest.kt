package com.thomaskioko.tvmaniac.presentation.discover

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.shows.testing.FakeShowsRepository
import com.thomaskioko.tvmaniac.tmdb.testing.FakeShowImagesRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ShowsStateMachineTest {

    private val traktRepository = FakeShowsRepository()
    private val imagesRepository = FakeShowImagesRepository()
    private val stateMachine = DiscoverStateMachine(traktRepository, imagesRepository)

    @Test
    fun initial_state_emits_expected_result() = runTest {
        traktRepository.setTrendingResult(Either.Right(categoryResult(1)))
        traktRepository.setPopularResult(Either.Right(categoryResult(3)))
        traktRepository.setAnticipatedResult(Either.Right(categoryResult(4)))
        traktRepository.setFeaturedResult(Either.Right(categoryResult(5)))

        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe ShowsLoaded(result = showResult)
        }
    }

    @Test
    fun on_category_error_emits_expected_result() = runTest {
        traktRepository.setFeaturedResult(Either.Left(DefaultError("Something went wrong")))
        traktRepository.setAnticipatedResult(Either.Left(DefaultError("Something went wrong")))
        traktRepository.setPopularResult(Either.Left(DefaultError("Something went wrong")))
        traktRepository.setTrendingResult(Either.Left(DefaultError("Something went wrong")))

        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe ShowsLoaded(errorShowResult)
        }
    }
}
