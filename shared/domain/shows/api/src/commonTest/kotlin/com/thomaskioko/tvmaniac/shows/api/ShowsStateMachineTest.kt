package com.thomaskioko.tvmaniac.shows.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.util.network.DefaultError
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbRepository
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktRepository
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class ShowsStateMachineTest {

    private val traktRepository = FakeTraktRepository()
    private val tmdbRepository = FakeTmdbRepository()
    private val stateMachine = ShowsStateMachine(traktRepository, tmdbRepository)

    @Test
    fun initial_state_emits_expected_result() = runBlockingTest {

        traktRepository.setTrendingResult(Either.Right(categoryResult(1)))
        traktRepository.setPopularResult(Either.Right(categoryResult(3)))
        traktRepository.setAnticipatedResult(Either.Right(categoryResult(4)))
        traktRepository.setFeaturedResult(Either.Right(categoryResult(5)))

        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe ShowsLoaded(result = showResult)
            awaitItem() shouldBe ShowsLoaded(result = showResult)
        }
    }

    @Test
    fun on_category_error_emits_expected_result() = runBlockingTest {

        traktRepository.setFeaturedResult( Either.Left(DefaultError(Throwable("Something went wrong"))))
        traktRepository.setAnticipatedResult( Either.Left(DefaultError(Throwable("Something went wrong"))))
        traktRepository.setPopularResult( Either.Left(DefaultError(Throwable("Something went wrong"))))
        traktRepository.setTrendingResult( Either.Left(DefaultError(Throwable("Something went wrong"))))

        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe ShowsLoaded(emptyShowResult)
            awaitItem() shouldBe ShowsLoaded(emptyShowResult)
        }
    }
}
