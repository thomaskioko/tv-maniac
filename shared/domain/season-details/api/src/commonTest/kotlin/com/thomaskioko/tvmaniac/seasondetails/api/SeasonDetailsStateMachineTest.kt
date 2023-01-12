package com.thomaskioko.tvmaniac.seasondetails.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.seasonDetails
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SeasonDetailsStateMachineTest {

    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val stateMachine = SeasonDetailsStateMachine(seasonDetailsRepository, episodeRepository)

    @Test
    fun initial_state_emits_expected_result() = runTest {
        stateMachine.state.test {

            stateMachine.dispatch(LoadSeasonDetails(1231))

            awaitItem() shouldBe Loading
        }
    }

    @Test
    fun onLoadSeasonDetails_correct_state_is_emitted() = runTest {
        stateMachine.state.test {
            seasonDetailsRepository.setSeasonDetails(Either.Right(seasonDetails))

            stateMachine.dispatch(LoadSeasonDetails(1231))

            awaitItem() shouldBe Loading
            awaitItem() shouldBe seasonDetailsLoaded //Loading State
            awaitItem() shouldBe seasonDetailsLoaded // SeasonDetails State
        }
    }

}




