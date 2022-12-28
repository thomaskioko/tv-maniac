package com.thomaskioko.tvmaniac.details.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.core.util.network.DefaultError
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.details.api.SeasonState.SeasonsError
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersError
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktRepository
import io.kotest.matchers.shouldBe
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
    fun initial_state_emits_expected_result() = runBlockingTest {
        stateMachine.state.test {

            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Right(trailers))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe initialShowDetailsLoaded // Show data loaded
            awaitItem() shouldBe seasonsShowDetailsLoaded //Seasons content updated
            awaitItem() shouldBe similarShowDetailsLoaded //Similar Shows content updated
            awaitItem() shouldBe trailerShowDetailsLoaded //Trailer content updated
            awaitItem() shouldBe trailerShowDetailsLoaded //Trailer content updated
        }
    }

    @Test
    fun error_loading_similarShows_emits_expected_result() = runBlockingTest {
        stateMachine.state.test {

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            trailerRepository.setTrailerResult(Either.Right(trailers))
            similarShowsRepository.setSimilarShowsResult(Either.Left(DefaultError(Throwable(errorMessage))))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe initialShowDetailsLoaded // Show data loaded
            awaitItem() shouldBe seasonsShowDetailsLoaded //Seasons content updated
            awaitItem() shouldBe similarShowDetailsLoaded //Similar Shows content updated
            awaitItem() shouldBe similarShowsErrorState //Trailer content updated
            awaitItem() shouldBe similarShowsErrorState //Similar shows content updated
        }
    }

    @Test
    fun error_loading_trailers_emits_expected_result() = runBlockingTest {
        stateMachine.state.test {

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(Either.Right(seasons))
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Left(DefaultError(Throwable(errorMessage))))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe initialShowDetailsLoaded // Show data loaded
            awaitItem() shouldBe seasonsShowDetailsLoaded //Seasons content updated
            awaitItem() shouldBe similarShowDetailsLoaded //Similar Shows content updated
                .copy(trailerState = TrailersError(errorMessage))
            awaitItem() shouldBe trailerErrorState //Trailer content updated
            awaitItem() shouldBe trailerShowDetailsLoaded //Trailer content updated
                .copy(trailerState = TrailersError(null))
        }
    }

    @Test
    fun error_loading_seasons_emits_expected_result() = runBlockingTest {
        stateMachine.state.test {

            val errorMessage = "Oppsy. Something went wrong"
            traktRepository.setShowResult(Either.Right(selectedShow))
            seasonsRepository.setSeasonsResult(
                Either.Left(
                    DefaultError(
                        Throwable(errorMessage)
                    )
                )
            )
            similarShowsRepository.setSimilarShowsResult(Either.Right(similarShowResult))
            trailerRepository.setTrailerResult(Either.Right(trailers))

            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe initialShowDetailsLoaded // Show data loaded
            awaitItem() shouldBe seasonsShowDetailsLoaded //Seasons content updated
                .copy(
                    seasonState = SeasonsError(errorMessage)
                )
            awaitItem() shouldBe similarShowDetailsLoaded //Similar Shows content updated
                .copy(
                    seasonState = SeasonsError(errorMessage)
                )
            awaitItem() shouldBe trailerShowDetailsLoaded //Trailer content updated
                .copy(
                    seasonState = SeasonsError(errorMessage)
                )
            awaitItem() shouldBe trailerShowDetailsLoaded //Trailer content updated
                .copy(
                    seasonState = SeasonsError(errorMessage)
                )
        }
    }

    @Test
    fun error_state_emits_expected_result() = runBlockingTest {
        stateMachine.state.test {

            traktRepository.setShowResult(
                result = Either.Left(DefaultError(Throwable("Oppsy. Something went wrong")))
            )
            stateMachine.dispatch(LoadShowDetails(84958))

            awaitItem() shouldBe ShowDetailsState.Loading
            awaitItem() shouldBe ShowDetailsState.ShowDetailsError("Oppsy. Something went wrong")
        }
    }
}