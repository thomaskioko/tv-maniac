package com.thomaskioko.tvmaniac.details.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.util.network.DefaultError
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.details.api.SeasonState.SeasonsError
import com.thomaskioko.tvmaniac.details.api.ShowDetailsState.ShowDetailsError
import com.thomaskioko.tvmaniac.details.api.SimilarShowsState.SimilarShowsError
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersError
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ShowDetailsStateMachineTest {

    private val seasonsRepository = FakeSeasonDetailsRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val traktRepository = FakeTraktRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()

    private val stateMachine = ShowDetailsStateMachine(
        traktRepository = traktRepository,
        trailerRepository = trailerRepository,
        seasonDetailsRepository = seasonsRepository,
        similarShowsRepository = similarShowsRepository
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
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Right(trailers))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded
            awaitItem() shouldBe seasonsShowDetailsLoaded
            awaitItem() shouldBe trailerShowDetailsLoaded
            awaitItem() shouldBe similarShowLoaded
        }
    }

    @Test
    fun error_loading_similarShows_emits_expected_result() = runTest {
        stateMachine.state.test {

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            similarShowsRepository.setSimilarShowsResult(Either.Left(DefaultError(Throwable(errorMessage))))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded // Show data loaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                similarShowsState = SimilarShowsError("Oppsy. Something went wrong"),
            )
        }
    }

    @Test
    fun error_loading_trailers_emits_expected_result() = runTest {
        stateMachine.state.test {

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            trailerRepository.setTrailerResult(Either.Left(DefaultError(Throwable(errorMessage))))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded // Show data loaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                trailerState = TrailersError("Oppsy. Something went wrong")
            )
        }
    }

    @Test
    fun error_loading_seasons_emits_expected_result() = runTest {
        stateMachine.state.test {

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(Either.Left(DefaultError(Throwable(errorMessage))))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded // Show data loaded
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = SeasonsError(errorMessage)
            )
        }
    }

    @Test
    fun error_state_emits_expected_result() = runTest {
        stateMachine.state.test {

            traktRepository.setShowResult(Either.Left(DefaultError(Throwable("Oppsy. Something went wrong"))))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe ShowDetailsError("Oppsy. Something went wrong")
        }
    }
}