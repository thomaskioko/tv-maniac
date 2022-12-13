package com.thomaskioko.tvmaniac.domain.following.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktRepository
import com.thomaskioko.tvmaniac.trakt.testing.cachedShowResult
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class FollowingStateMachineTest {

    private val traktRepository = FakeTraktRepository()
    private val stateMachine = FollowingStateMachine(traktRepository)

    @Test
    fun initial_state_emits_expected_result() = runBlockingTest {

        traktRepository.setFollowedResult(Resource.Success(data = cachedShowResult))

        stateMachine.state.test {
            awaitItem() shouldBe LoadingShows
            awaitItem() shouldBe FollowingContent(list = followedShows)
            awaitItem() shouldBe FollowingContent(list = emptyList())
        }
    }

}