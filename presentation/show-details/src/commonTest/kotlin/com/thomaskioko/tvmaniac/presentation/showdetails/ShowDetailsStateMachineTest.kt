package com.thomaskioko.tvmaniac.presentation.showdetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeShowsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // TODO:: Fix test
internal class ShowDetailsStateMachineTest {

    private val exceptionHandler = object : ExceptionHandler {
        override fun resolveError(throwable: Throwable): String = "Something went wrong"
    }

    private val seasonsRepository = FakeSeasonsRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val traktRepository = FakeShowsRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()
    private val watchlistRepository = FakeWatchlistRepository()

    private val stateMachine = ShowDetailsStateMachine(
        traktShowId = 84958,
        showsRepository = traktRepository,
        trailerRepository = trailerRepository,
        seasonsRepository = seasonsRepository,
        similarShowsRepository = similarShowsRepository,
        watchlistRepository = watchlistRepository,
        exceptionHandler = exceptionHandler,
    )

    @Test
    fun initial_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
        }
    }

    @Test
    fun loadingData_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(
                StoreReadResponse.Data(
                    value = seasons,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            similarShowsRepository.setSimilarShowsResult(
                StoreReadResponse.Data(
                    value = similarShowResult,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            trailerRepository.setTrailerResult(
                StoreReadResponse.Data(
                    value = trailers,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
                trailerState = trailerShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
                trailerState = trailerShowDetailsLoaded,
                similarShowsState = similarShowLoaded,
            )
        }
    }

    @Test
    fun error_loading_similarShows_emits_expected_result() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(
                StoreReadResponse.Data(
                    value = seasons,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            trailerRepository.setTrailerResult(
                StoreReadResponse.Data(
                    value = trailers,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            similarShowsRepository.setSimilarShowsResult(
                StoreReadResponse.Error.Message(
                    message = errorMessage,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
                trailerState = trailerShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
                trailerState = trailerShowDetailsLoaded,
                similarShowsState = SimilarShowsState.SimilarShowsError(errorMessage),
            )
        }
    }

    @Test
    fun error_loading_trailers_emits_expected_result() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(
                StoreReadResponse.Data(
                    value = seasons,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            similarShowsRepository.setSimilarShowsResult(
                StoreReadResponse.Data(
                    value = similarShowResult,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            trailerRepository.setTrailerResult(
                StoreReadResponse.Error.Message(
                    message = errorMessage,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
                trailerState = TrailersState.TrailersError(errorMessage),
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
                similarShowsState = similarShowLoaded,
                trailerState = TrailersState.TrailersError(errorMessage),
            )
        }
    }

    @Test
    fun error_loading_seasons_emits_expected_result() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            trailerRepository.setTrailerResult(
                StoreReadResponse.Data(
                    value = trailers,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            similarShowsRepository.setSimilarShowsResult(
                StoreReadResponse.Data(
                    value = similarShowResult,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            seasonsRepository.setSeasonsResult(
                StoreReadResponse.Error.Message(
                    message = errorMessage,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = SeasonState.SeasonsError(errorMessage),
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = SeasonState.SeasonsError(errorMessage),
                trailerState = trailerShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = SeasonState.SeasonsError(errorMessage),
                trailerState = trailerShowDetailsLoaded,
                similarShowsState = similarShowLoaded,
            )
        }
    }

    @Test
    fun error_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            traktRepository.setShowResult(Either.Left(DefaultError(errorMessage)))
            seasonsRepository.setSeasonsResult(
                StoreReadResponse.Error.Message(
                    message = errorMessage,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            similarShowsRepository.setSimilarShowsResult(
                StoreReadResponse.Data(
                    value = similarShowResult,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
            trailerRepository.setTrailerResult(
                StoreReadResponse.Data(
                    value = trailers,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe ShowDetailsState.ShowDetailsError(errorMessage)
        }
    }
}
