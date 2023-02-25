package com.thomaskioko.tvmaniac.details.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.util.network.DefaultError
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.details.api.SeasonState.SeasonsError
import com.thomaskioko.tvmaniac.details.api.SimilarShowsState.SimilarShowsError
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersError
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktShowRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ShowDetailsStateMachineTest {

    private val seasonsRepository = FakeSeasonDetailsRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val traktRepository = FakeTraktShowRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()

    private val stateMachine = ShowDetailsStateMachine(
        traktShowRepository = traktRepository,
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

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            trailerRepository.setTrailerResult(Either.Right(trailers))
            similarShowsRepository.setSimilarShowsResult(
                Either.Left(
                    DefaultError(
                        Throwable(
                            errorMessage
                        )
                    )
                )
            )

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
                trailerState = trailerShowDetailsLoaded,
                similarShowsState = SimilarShowsError(errorMessage),
            )
        }
    }

    @Test
    fun error_loading_trailers_emits_expected_result() = runTest {
        stateMachine.state.test {

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Left(DefaultError(Throwable(errorMessage))))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded.copy(
                seasonState = seasonsShowDetailsLoaded,
                similarShowsState = similarShowLoaded,
                trailerState = TrailersError(errorMessage),
            )
        }
    }

    @Test
    fun error_loading_seasons_emits_expected_result() = runTest {
        stateMachine.state.test {

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            trailerRepository.setTrailerResult(Either.Right(trailers))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            seasonsRepository.setSeasonsResult(Either.Left(DefaultError(Throwable(errorMessage))))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe showDetailsLoaded.copy(
                similarShowsState = similarShowLoaded,
                trailerState = trailerShowDetailsLoaded,
                seasonState = SeasonsError(errorMessage),
            )
        }
    }

    @Test
    fun error_state_emits_expected_result() = runTest {
        stateMachine.state.test {

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Left(DefaultError(Throwable(errorMessage))))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Right(trailers))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe ShowDetailsState.ShowDetailsLoaded(
                showState = ShowState.ShowError(errorMessage),
                seasonState = seasonsShowDetailsLoaded,
                trailerState = trailerShowDetailsLoaded,
                similarShowsState = similarShowLoaded,
                followShowState = FollowShowsState.Idle
            )
        }
    }
}