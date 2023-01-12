package com.thomaskioko.tvmaniac.domain.following.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktRepository
import com.thomaskioko.tvmaniac.trakt.testing.cachedShowResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FollowingStateMachineTest {

    private val traktRepository = FakeTraktRepository()
    private val stateMachine = FollowingStateMachine(traktRepository)

    @Test
    fun initial_state_emits_expected_result() = runTest {

        traktRepository.setFollowedResult(Either.Right(data = cachedShowResult))

        stateMachine.state.test {
            awaitItem() shouldBe LoadingShows
            awaitItem() shouldBe FollowingContent(list = followedShows)
        }
    }

}