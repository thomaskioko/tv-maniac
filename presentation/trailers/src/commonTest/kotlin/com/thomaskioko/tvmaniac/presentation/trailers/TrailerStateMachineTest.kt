package com.thomaskioko.tvmaniac.presentation.trailers

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.presentation.trailers.model.Trailer
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
internal class TrailerStateMachineTest {

    private val repository = FakeTrailerRepository()
    private val stateMachine = TrailersStateMachine(
        traktShowId = 84958,
        repository = repository,
    )

    @Test
    fun reloadTrailers_emits_expected_result() = runTest {
        stateMachine.state.test {
            repository.setTrailerList(trailers)

            repository.setTrailerResult(
                StoreReadResponse.Error.Message(
                    message = "Something went wrong.",
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )

            awaitItem() shouldBe LoadingTrailers
            awaitItem() shouldBe TrailerError("Something went wrong.")

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
