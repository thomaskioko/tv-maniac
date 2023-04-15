package com.thomaskioko.tvmaniac.domain.following.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.domain.following.FollowingContent
import com.thomaskioko.tvmaniac.domain.following.FollowingStateMachine
import com.thomaskioko.tvmaniac.domain.following.LoadingShows
import com.thomaskioko.tvmaniac.shows.testing.FakeShowsRepository
import com.thomaskioko.tvmaniac.shows.testing.cachedShowResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FollowingStateMachineTest {

    private val traktRepository = FakeShowsRepository()
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