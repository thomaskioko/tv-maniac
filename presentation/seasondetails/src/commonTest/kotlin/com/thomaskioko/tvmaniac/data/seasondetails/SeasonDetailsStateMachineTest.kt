package com.thomaskioko.tvmaniac.data.seasondetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.presentation.seasondetails.Loading
import com.thomaskioko.tvmaniac.presentation.seasondetails.LoadingError
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsStateMachine
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.seasons.testing.seasonDetails
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // TODO:: Fix test
class SeasonDetailsStateMachineTest {

    private val seasonDetailsRepository = FakeSeasonsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val stateMachine = SeasonDetailsStateMachine(
        traktId = 1231,
        episodeRepository = episodeRepository,
        seasonDetailsRepository = seasonDetailsRepository,
    )

    @Test
    fun onLoadSeasonDetails_correct_state_is_emitted() = runTest {
        stateMachine.state.test {
            seasonDetailsRepository.setSeasonDetails(Either.Right(seasonDetails))

            awaitItem() shouldBe Loading
            awaitItem() shouldBe seasonDetailsLoaded
        }
    }

    @Test
    fun onLoadSeasonDetails_andErrorOccurs_correctStateIsEmitted() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            seasonDetailsRepository.setSeasonDetails(Either.Left(DefaultError(errorMessage)))

            awaitItem() shouldBe Loading
            awaitItem() shouldBe LoadingError(errorMessage)
        }
    }
}
