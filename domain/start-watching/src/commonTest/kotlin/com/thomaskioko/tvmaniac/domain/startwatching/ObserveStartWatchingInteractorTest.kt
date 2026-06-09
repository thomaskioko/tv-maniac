package com.thomaskioko.tvmaniac.domain.startwatching

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ObserveStartWatchingInteractorTest {

    private val repository = FakeStartWatchingRepository()
    private val interactor = ObserveStartWatchingInteractor(repository)

    @Test
    fun `should emit start watching shows when repository updates`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().shouldBeEmpty()

            repository.setStartWatchingShows(shows)

            awaitItem() shouldContainExactly shows
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        val shows = listOf(
            StartWatchingShow(
                showId = 1,
                tmdbId = 1,
                title = "Breaking Bad",
                posterPath = "/1.jpg",
                year = "2008",
                inLibrary = true,
            ),
            StartWatchingShow(
                showId = 2,
                tmdbId = 2,
                title = "Severance",
                posterPath = "/2.jpg",
                year = "2022",
                inLibrary = true,
            ),
        )
    }
}
