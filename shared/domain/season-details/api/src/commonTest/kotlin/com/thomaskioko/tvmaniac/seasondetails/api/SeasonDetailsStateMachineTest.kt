package com.thomaskioko.tvmaniac.seasondetails.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.cacheEpisodes
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.seasonDetails
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SeasonDetailsStateMachineTest {

    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val stateMachine = SeasonDetailsStateMachine(seasonDetailsRepository, episodeRepository)

    @Test
    fun initial_state_emits_expected_result() = runBlockingTest {
        seasonDetailsRepository.setSeasonDetails(Resource.Success(seasonDetails))
        episodeRepository.setEpisode(cacheEpisodes)

        stateMachine.state.test {
            stateMachine.dispatch(LoadSeasonDetails(1231))

            awaitItem() shouldBe Loading
            awaitItem() shouldBe showDetails
            awaitItem() shouldBe showDetails
        }
    }

}




