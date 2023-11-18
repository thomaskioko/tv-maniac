package com.thomaskioko.tvmaniac.presentation.showdetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.ServerError
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.Companion.EMPTY_DETAIL_STATE
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.SeasonsContent.Companion.EMPTY_SEASONS
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.SimilarShowsContent.Companion.EMPTY_SIMILAR_SHOWS
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.TrailersContent.Companion.EMPTY_TRAILERS
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeDiscoverRepository
import com.thomaskioko.tvmaniac.shows.testing.selectedShow
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
internal class ShowDetailsStateMachineTest {

    private val seasonsRepository = FakeSeasonsRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val discoverRepository = FakeDiscoverRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()
    private val watchlistRepository = FakeWatchlistRepository()

    private val stateMachine = ShowDetailsStateMachine(
        traktShowId = 84958,
        discoverRepository = discoverRepository,
        trailerRepository = trailerRepository,
        seasonsRepository = seasonsRepository,
        similarShowsRepository = similarShowsRepository,
        watchlistRepository = watchlistRepository,
    )

    @Test
    fun initial_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            discoverRepository.setShowById(selectedShow)

            awaitItem() shouldBe EMPTY_DETAIL_STATE.copy(
                show = show,
            )
        }
    }

    @Test
    fun loadingData_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            discoverRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Right(trailers))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe EMPTY_DETAIL_STATE
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
            discoverRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            trailerRepository.setTrailerResult(Either.Right(trailers))
            similarShowsRepository.setSimilarShowsResult(Either.Left(ServerError(errorMessage)))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe EMPTY_DETAIL_STATE
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
            discoverRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Left(ServerError(errorMessage)))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe EMPTY_DETAIL_STATE
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
            discoverRepository.setShowResult(Either.Right(selectedShow))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Right(trailers))
            seasonsRepository.setSeasonWithEpisodes(Either.Left(ServerError(errorMessage)))

            awaitItem() shouldBe EMPTY_DETAIL_STATE
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
            discoverRepository.setShowResult(Either.Left(ServerError(errorMessage)))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Right(trailers))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe EMPTY_DETAIL_STATE
            awaitItem() shouldBe EMPTY_DETAIL_STATE.copy(
                errorMessage = errorMessage,
            )
        }
    }
}
