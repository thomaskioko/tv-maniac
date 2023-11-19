package com.thomaskioko.tvmaniac.data.seasondetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeImageRepository
import com.thomaskioko.tvmaniac.presentation.seasondetails.Loading
import com.thomaskioko.tvmaniac.presentation.seasondetails.LoadingError
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsStateMachine
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.SeasonWithEpisodeList
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SeasonDetailsStateMachineTest {

    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val episodeImageRepository = FakeEpisodeImageRepository()
    private val stateMachine = SeasonDetailsStateMachine(
        traktId = 1231,
        episodeImageRepository = episodeImageRepository,
        seasonDetailsRepository = seasonDetailsRepository,
    )

    @Test
    fun onLoadSeasonDetails_correct_state_is_emitted() = runTest {
        stateMachine.state.test {
            seasonDetailsRepository.setCachedResults(SeasonWithEpisodeList)

            awaitItem() shouldBe Loading
            awaitItem() shouldBe seasonDetailsLoaded
        }
    }

    @Test
    fun onLoadSeasonDetails_andErrorOccurs_correctStateIsEmitted() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            seasonDetailsRepository.setCachedResults(SeasonWithEpisodeList)
            seasonDetailsRepository.setSeasonsResult(Either.Left(DefaultError(errorMessage)))

            awaitItem() shouldBe Loading
            awaitItem() shouldBe seasonDetailsLoaded
            awaitItem() shouldBe LoadingError(errorMessage)
        }
    }
}
