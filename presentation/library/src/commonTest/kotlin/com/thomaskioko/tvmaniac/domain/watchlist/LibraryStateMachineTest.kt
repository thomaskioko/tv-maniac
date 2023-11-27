package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryContent
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryStateMachine
import com.thomaskioko.tvmaniac.presentation.watchlist.LoadingShows
import com.thomaskioko.tvmaniac.watchlist.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.watchlist.testing.watchlistResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class LibraryStateMachineTest {

    private val repository = FakeLibraryRepository()
    private val stateMachine = LibraryStateMachine(repository)

    @Test
    fun initial_state_emits_expected_result() = runTest {
        repository.setFollowedResult(watchlistResult)

        stateMachine.state.test {
            awaitItem() shouldBe LoadingShows
            awaitItem() shouldBe LibraryContent(list = libraryItems)
        }
    }
}
