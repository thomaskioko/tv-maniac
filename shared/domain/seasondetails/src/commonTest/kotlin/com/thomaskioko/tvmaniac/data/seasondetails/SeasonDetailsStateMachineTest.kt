package com.thomaskioko.tvmaniac.data.seasondetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.seasonDetails
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeasonDetailsStateMachineTest {

    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val stateMachine = SeasonDetailsStateMachine(seasonDetailsRepository, episodeRepository)

    @Test
    fun onLoadSeasonDetails_correct_state_is_emitted() = runTest {
        stateMachine.state.test {
            seasonDetailsRepository.setSeasonDetails(Either.Right(seasonDetails))

            stateMachine.dispatch(LoadSeasonDetails(1231))

            awaitItem() shouldBe Loading
            awaitItem() shouldBe seasonDetailsLoaded
        }
    }

    @Test
    fun onLoadSeasonDetails_andErrorOccurs_correctStateIsEmitted() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            seasonDetailsRepository.setSeasonDetails(Either.Left(DefaultError(errorMessage)))

            stateMachine.dispatch(LoadSeasonDetails(1231))

            awaitItem() shouldBe Loading
            awaitItem() shouldBe LoadingError(errorMessage)
        }
    }

}




