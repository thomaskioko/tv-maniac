package com.thomaskioko.tvmaniac.domain.showdetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.seasondetails.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObserveContinueTrackingInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private lateinit var interactor: ObserveContinueTrackingInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveContinueTrackingInteractor(
            seasonDetailsRepository = seasonDetailsRepository,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit episodes given continue tracking result`() = runTest {
        val episode = EpisodeDetails(
            id = 1L,
            seasonId = 1L,
            name = "Pilot",
            seasonNumber = 1L,
            episodeNumber = 1L,
            runtime = 45L,
            overview = "Overview",
            voteAverage = 8.5,
            voteCount = 100L,
            stillPath = null,
            firstAired = null,
            isWatched = false,
            daysUntilAir = null,
            hasAired = true,
        )
        seasonDetailsRepository.setContinueTrackingResult(
            ContinueTrackingResult(
                episodes = persistentListOf(episode),
                currentSeasonNumber = 1L,
                currentSeasonId = 1L,
            ),
        )
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe persistentListOf(episode)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit empty list given null continue tracking result`() = runTest {
        seasonDetailsRepository.setContinueTrackingResult(null)
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe persistentListOf()
            cancelAndConsumeRemainingEvents()
        }
    }
}
