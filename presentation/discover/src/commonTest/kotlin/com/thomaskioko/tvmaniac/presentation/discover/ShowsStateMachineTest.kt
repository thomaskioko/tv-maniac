package com.thomaskioko.tvmaniac.presentation.discover

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.shows.testing.FakeShowsRepository
import com.thomaskioko.tvmaniac.tmdb.testing.FakeShowImagesRepository
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
internal class ShowsStateMachineTest {
    private val exceptionHandler = object : ExceptionHandler {
        override fun resolveError(throwable: Throwable): String {
            return "Opps!! Something went wrong"
        }
    }

    private val traktRepository = FakeShowsRepository()
    private val imagesRepository = FakeShowImagesRepository()
    private val stateMachine = DiscoverStateMachine(
        exceptionHandler,
        traktRepository,
        imagesRepository,
    )

    @Test
    fun initial_state_emits_expected_result() = runTest {
        traktRepository.setTrendingResult(
            StoreReadResponse.Data(
                value = categoryResult(1),
                origin = StoreReadResponseOrigin.Cache,
            ),
        )
        traktRepository.setPopularResult(
            StoreReadResponse.Data(
                value = categoryResult(2),
                origin = StoreReadResponseOrigin.Cache,
            ),
        )
        traktRepository.setAnticipatedResult(
            StoreReadResponse.Data(
                value = categoryResult(3),
                origin = StoreReadResponseOrigin.Cache,
            ),
        )
        traktRepository.setFeaturedResult(
            StoreReadResponse.Data(
                value = categoryResult(4),
                origin = StoreReadResponseOrigin.Cache,
            ),
        )

        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe discoverContent
        }
    }

    @Test
    fun on_category_error_emits_expected_result() = runTest {
        traktRepository.setFeaturedResult(
            StoreReadResponse.Error.Message(
                message = "Something went wrong",
                origin = StoreReadResponseOrigin.Cache,
            ),
        )
        traktRepository.setAnticipatedResult(
            StoreReadResponse.Error.Message(
                message = "Something went wrong",
                origin = StoreReadResponseOrigin.Cache,
            ),
        )
        traktRepository.setPopularResult(
            StoreReadResponse.Error.Message(
                message = "Something went wrong",
                origin = StoreReadResponseOrigin.Cache,
            ),
        )
        traktRepository.setTrendingResult(
            StoreReadResponse.Error.Message(
                message = "Something went wrong",
                origin = StoreReadResponseOrigin.Cache,
            ),
        )

        stateMachine.state.test {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe DiscoverContent(
                contentState = DiscoverContent.DataLoaded(),
            )
        }
    }
}
