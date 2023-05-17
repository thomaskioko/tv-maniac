package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.presentation.watchlist.LoadingShows
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistContent
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistStateMachine
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import com.thomaskioko.tvmaniac.watchlist.testing.watchlistResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class WatchlistStateMachineTest {

    private val repository = FakeWatchlistRepository()
    private val stateMachine = WatchlistStateMachine(repository)

    @Test
    fun initial_state_emits_expected_result() = runTest {
        repository.setFollowedResult(Either.Right(data = watchlistResult))

        stateMachine.state.test {
            awaitItem() shouldBe LoadingShows
            awaitItem() shouldBe WatchlistContent(list = watchlistItems)
        }
    }
}
