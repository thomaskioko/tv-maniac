package com.thomaskioko.tvmaniac.data.seasondetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeImageRepository
import com.thomaskioko.tvmaniac.presentation.seasondetails.Loading
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsScreenModel
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.SeasonWithEpisodeList
import com.thomaskioko.tvmaniac.util.model.DefaultError
import com.thomaskioko.tvmaniac.util.model.Either
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeasonScreenModelTest {

    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val episodeImageRepository = FakeEpisodeImageRepository()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var screenModel: SeasonDetailsScreenModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        screenModel = SeasonDetailsScreenModel(
            traktId = 1231,
            episodeImageRepository = episodeImageRepository,
            seasonDetailsRepository = seasonDetailsRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onLoadSeasonDetails_correct_state_is_emitted() = runTest {
        screenModel.state.test {
            seasonDetailsRepository.setCachedResults(SeasonWithEpisodeList)

            awaitItem() shouldBe Loading
            awaitItem() shouldBe seasonDetailsLoaded
        }
    }

    @Test
    fun onLoadSeasonDetails_andErrorOccurs_correctStateIsEmitted() = runTest {
        screenModel.state.test {
            val errorMessage = "Something went wrong"
            seasonDetailsRepository.setCachedResults(SeasonWithEpisodeList)
            seasonDetailsRepository.setSeasonsResult(Either.Left(DefaultError(errorMessage)))

            awaitItem() shouldBe Loading
            awaitItem() shouldBe seasonDetailsLoaded
            awaitItem() shouldBe seasonDetailsLoaded
                .copy(errorMessage = errorMessage)
        }
    }
}
