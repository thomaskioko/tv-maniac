package com.thomaskioko.tvmaniac.presentation.trailers

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.presentation.trailers.model.Trailer
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class TrailerStateMachineTest {

    private val repository = FakeTrailerRepository()
    private val stateMachine = TrailersStateMachine(
        traktShowId = 84958,
        repository = repository,
    )

    @Test
    fun reloadTrailers_emits_expected_result() = runTest {
        stateMachine.state.test {
            repository.setTrailerResult(Either.Right(trailers))

            stateMachine.dispatch(ReloadTrailers)

            awaitItem() shouldBe LoadingTrailers
            awaitItem() shouldBe TrailersContent(
                selectedVideoKey = "Fd43V",
                trailersList = listOf(
                    Trailer(
                        showId = 84958,
                        key = "Fd43V",
                        name = "Some title",
                        youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
                    ),
                ),
            )
        }
    }
}
