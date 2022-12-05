package com.thomaskioko.tvmaniac.shows.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.util.network.Resource
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

        traktRepository.setCategoryResult(
            result = Resource.Success(data = categoryResult)
        )
        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe ShowsLoaded(
                result = showResult.copy(
                    updateState = ShowUpdateState.IDLE
                )
            )
            awaitItem() shouldBe ShowsLoaded(
                result = showResult.copy(
                    updateState = ShowUpdateState.IDLE
                )
            )
        }
    }

    @Test
    fun on_category_error_emits_expected_result() = runBlockingTest {

        traktRepository.setCategoryResult(
            result = Resource.Error(errorMessage = "Something went wrong")
        )
        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe ShowsLoaded(emptyShowResult)
            awaitItem() shouldBe ShowsLoaded(emptyShowResult)
        }
    }
}
