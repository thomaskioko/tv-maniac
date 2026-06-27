package com.thomaskioko.tvmaniac.domain.showdetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.domain.showdetails.model.Show
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObserveSimilarShowsInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )
    private val similarShowsRepository = FakeSimilarShowsRepository()
    private lateinit var interactor: ObserveSimilarShowsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveSimilarShowsInteractor(
            similarShowsRepository = similarShowsRepository,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit mapped show list given similar shows data`() = runTest {
        similarShowsRepository.setSimilarShowsResult(
            listOf(
                SimilarShows(
                    show_id = Id<TmdbId>(84958L),
                    tmdb_id = Id<TmdbId>(84958L),
                    name = "Similar Show",
                    poster_path = "/poster.jpg",
                    backdrop_path = "/backdrop.jpg",
                    trakt_id = Id<TraktId>(12345L),
                    in_library = 0L,
                ),
            ),
        )
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe listOf(
                Show(
                    showId = 84958L,
                    title = "Similar Show",
                    posterImageUrl = "/poster.jpg",
                    backdropImageUrl = "/backdrop.jpg",
                    isInLibrary = false,
                ),
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit empty list given no similar shows`() = runTest {
        similarShowsRepository.setSimilarShowsResult(emptyList())
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe emptyList()
            cancelAndConsumeRemainingEvents()
        }
    }
}
