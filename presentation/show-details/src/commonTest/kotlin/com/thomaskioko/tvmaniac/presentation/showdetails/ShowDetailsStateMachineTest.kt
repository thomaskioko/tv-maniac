package com.thomaskioko.tvmaniac.presentation.showdetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.SeasonsContent.Companion.EMPTY_SEASONS
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.SimilarShowsContent.Companion.EMPTY_SIMILAR_SHOWS
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.TrailersContent.Companion.EMPTY_TRAILERS
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeDiscoverRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
internal class ShowDetailsStateMachineTest {

    private val seasonsRepository = FakeSeasonsRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val traktRepository = FakeDiscoverRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()
    private val watchlistRepository = FakeWatchlistRepository()

    private val stateMachine = ShowDetailsStateMachine(
        traktShowId = 84958,
        discoverRepository = traktRepository,
        trailerRepository = trailerRepository,
        seasonsRepository = seasonsRepository,
        similarShowsRepository = similarShowsRepository,
        watchlistRepository = watchlistRepository,
    )

    @Test
    fun initial_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsLoaded.EMPTY_DETAIL_STATE
        }
    }

    @Test
    fun loadingData_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            traktRepository.setShowResult(
                StoreReadResponse.Data(
                    value = selectedShow,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
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

            awaitItem() shouldBe ShowDetailsLoaded.EMPTY_DETAIL_STATE
            awaitItem() shouldBe showDetailsLoaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = seasonsShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = seasonsShowDetailsLoaded,
                trailersContent = trailerShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = seasonsShowDetailsLoaded,
                trailersContent = trailerShowDetailsLoaded,
                similarShowsContent = similarShowLoaded,
            )
        }
    }

    @Test
    fun error_loading_similarShows_emits_expected_result() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            traktRepository.setShowResult(
                StoreReadResponse.Data(
                    value = selectedShow,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
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

            awaitItem() shouldBe ShowDetailsLoaded.EMPTY_DETAIL_STATE
            awaitItem() shouldBe showDetailsLoaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = seasonsShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = seasonsShowDetailsLoaded,
                trailersContent = trailerShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = seasonsShowDetailsLoaded,
                trailersContent = trailerShowDetailsLoaded,
                similarShowsContent = EMPTY_SIMILAR_SHOWS.copy(
                    errorMessage = errorMessage,
                ),
            )
        }
    }

    @Test
    fun error_loading_trailers_emits_expected_result() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            traktRepository.setShowResult(
                StoreReadResponse.Data(
                    value = selectedShow,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
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

            awaitItem() shouldBe ShowDetailsLoaded.EMPTY_DETAIL_STATE
            awaitItem() shouldBe showDetailsLoaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = seasonsShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = seasonsShowDetailsLoaded,
                trailersContent = EMPTY_TRAILERS.copy(
                    errorMessage = errorMessage,
                ),
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = seasonsShowDetailsLoaded,
                similarShowsContent = similarShowLoaded,
                trailersContent = EMPTY_TRAILERS.copy(
                    errorMessage = errorMessage,
                ),
            )
        }
    }

    @Test
    fun error_loading_seasons_emits_expected_result() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            traktRepository.setShowResult(
                StoreReadResponse.Data(
                    value = selectedShow,
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

            awaitItem() shouldBe ShowDetailsLoaded.EMPTY_DETAIL_STATE
            awaitItem() shouldBe showDetailsLoaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = EMPTY_SEASONS.copy(
                    errorMessage = errorMessage,
                ),
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = EMPTY_SEASONS.copy(
                    errorMessage = errorMessage,
                ),
                trailersContent = trailerShowDetailsLoaded,
            )
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonsContent = EMPTY_SEASONS.copy(
                    errorMessage = errorMessage,
                ),
                trailersContent = trailerShowDetailsLoaded,
                similarShowsContent = similarShowLoaded,
            )
        }
    }

    @Test
    fun error_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            val errorMessage = "Something went wrong"
            traktRepository.setShowResult(
                StoreReadResponse.Error.Message(
                    message = errorMessage,
                    origin = StoreReadResponseOrigin.Cache,
                ),
            )
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

            awaitItem() shouldBe ShowDetailsLoaded.EMPTY_DETAIL_STATE
            awaitItem() shouldBe ShowDetailsLoaded.EMPTY_DETAIL_STATE.copy(
                errorMessage = errorMessage,
            )
        }
    }
}
