package com.thomaskioko.tvmaniac.domain.showdetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonId
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.domain.showdetails.model.Season
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObserveSeasonsInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )
    private val seasonsRepository = FakeSeasonsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private lateinit var interactor: ObserveSeasonsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveSeasonsInteractor(
            seasonsRepository = seasonsRepository,
            episodeRepository = episodeRepository,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit seasons with watch progress given seasons and progress data`() = runTest {
        val season = ShowSeasons(
            show_id = Id<TmdbId>(84958L),
            season_id = Id<SeasonId>(1L),
            season_title = "Season 1",
            season_number = 1L,
        )
        val progress = SeasonWatchProgress(
            showId = 84958L,
            seasonNumber = 1L,
            watchedCount = 3,
            totalCount = 10,
        )
        seasonsRepository.setSeasonsResult(listOf(season))
        episodeRepository.setAllSeasonsWatchProgress(listOf(progress))
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe listOf(
                Season(
                    seasonId = 1L,
                    tvShowId = 84958L,
                    name = "Season 1",
                    seasonNumber = 1L,
                    watchedCount = 3,
                    totalCount = 10,
                ),
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit seasons with zero progress given no watch progress data`() = runTest {
        val season = ShowSeasons(
            show_id = Id<TmdbId>(84958L),
            season_id = Id<SeasonId>(1L),
            season_title = "Season 1",
            season_number = 1L,
        )
        seasonsRepository.setSeasonsResult(listOf(season))
        episodeRepository.setAllSeasonsWatchProgress(emptyList())
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe listOf(
                Season(
                    seasonId = 1L,
                    tvShowId = 84958L,
                    name = "Season 1",
                    seasonNumber = 1L,
                    watchedCount = 0,
                    totalCount = 0,
                ),
            )
            cancelAndConsumeRemainingEvents()
        }
    }
}
