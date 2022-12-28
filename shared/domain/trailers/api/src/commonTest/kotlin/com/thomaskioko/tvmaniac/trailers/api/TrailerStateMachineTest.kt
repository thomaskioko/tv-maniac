package com.thomaskioko.tvmaniac.trailers.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.domain.trailers.api.LoadTrailers
import com.thomaskioko.tvmaniac.domain.trailers.api.LoadingTrailers
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailersLoaded
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailersStateMachine
import com.thomaskioko.tvmaniac.domain.trailers.api.model.Trailer
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class TrailerStateMachineTest {

    private val trailerRepository = FakeTrailerRepository()
    private val stateMachine = TrailersStateMachine(trailerRepository)

    @Test
    fun loadTrailers_state_emits_expected_result() = runBlockingTest {
        stateMachine.state.test {
            trailerRepository.setTrailerResult(Either.Right(trailers))

            stateMachine.dispatch(LoadTrailers(84958, "Fd43V"))

            awaitItem() shouldBe LoadingTrailers
            awaitItem() shouldBe TrailersLoaded(
                selectedVideoKey= "Fd43V",
                trailersList = listOf(
                    Trailer(
                        showId=84958,
                        key="Fd43V",
                        name="Some title",
                        youtubeThumbnailUrl="https://i.ytimg.com/vi/Fd43V/hqdefault.jpg"
                    )
                )
            )
        }
    }
}