package com.thomaskioko.tvmaniac.shared.domain.discover

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.util.network.DefaultError
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbRepository
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktShowRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ShowsStateMachineTest {

    private val traktRepository = FakeTraktShowRepository()
    private val tmdbRepository = FakeTmdbRepository()
    private val stateMachine = DiscoverStateMachine(traktRepository, tmdbRepository)


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

        traktRepository.setFeaturedResult(Either.Left(DefaultError(Throwable("Something went wrong"))))
        traktRepository.setAnticipatedResult(Either.Left(DefaultError(Throwable("Something went wrong"))))
        traktRepository.setPopularResult(Either.Left(DefaultError(Throwable("Something went wrong"))))
        traktRepository.setTrendingResult(Either.Left(DefaultError(Throwable("Something went wrong"))))

        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe ShowsLoaded(errorShowResult)
        }
    }
}
