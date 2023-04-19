package com.thomaskioko.tvmaniac.data.trailers

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.domain.trailers.LoadTrailers
import com.thomaskioko.tvmaniac.domain.trailers.LoadingTrailers
import com.thomaskioko.tvmaniac.domain.trailers.TrailersLoaded
import com.thomaskioko.tvmaniac.domain.trailers.TrailersStateMachine
import com.thomaskioko.tvmaniac.domain.trailers.model.Trailer
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class TrailerStateMachineTest {

    private val trailerRepository = FakeTrailerRepository()
    private val stateMachine = TrailersStateMachine(trailerRepository)

    @Test
    fun loadTrailers_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            trailerRepository.setTrailerResult(Either.Right(trailers))

            stateMachine.dispatch(LoadTrailers(84958, "Fd43V"))

            awaitItem() shouldBe LoadingTrailers
            awaitItem() shouldBe TrailersLoaded(
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
