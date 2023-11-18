package com.thomaskioko.tvmaniac.presentation.trailers

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.ServerError
import com.thomaskioko.tvmaniac.presentation.trailers.model.Trailer
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class TrailerStateMachineTest {

    private val repository = FakeTrailerRepository()
    private val stateMachine = TrailersStateMachine(
        traktShowId = 84958,
        repository = repository,
    )

    @Test
    fun `given result is success correct state is emitted`() = runTest {
        stateMachine.state.test {
            repository.setTrailerList(trailers)

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

    @Test
    fun `given reload is clicked then correct state is emitted`() = runTest {
        stateMachine.state.test {
            repository.setTrailerList(trailers)

            repository.setTrailerResult(Either.Left(ServerError("Something went wrong.")))

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

            awaitItem() shouldBe TrailerError("Something went wrong.")

            stateMachine.dispatch(ReloadTrailers)

            repository.setTrailerResult(Either.Right(trailers))

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
