package com.thomaskioko.tvmaniac.domain.recentlywatched

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ObserveRecentlyWatchedInteractorTest {

    private val repository = FakeEpisodeRepository()
    private val interactor = ObserveRecentlyWatchedInteractor(repository)

    @Test
    fun `should emit recently watched episodes when repository updates`() = runTest {
        interactor(ObserveRecentlyWatchedInteractor.Param())

        interactor.flow.test {
            awaitItem().shouldBeEmpty()

            repository.setRecentlyWatched(episodes)

            awaitItem() shouldContainExactly episodes
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        val episodes = listOf(
            RecentlyWatchedEpisode(
                showId = 1,
                showTmdbId = 1,
                showTitle = "Breaking Bad",
                posterPath = "/1.jpg",
                seasonNumber = 1,
                episodeNumber = 2,
                episodeTitle = "Cat's in the Bag...",
                watchedAt = 2_000L,
            ),
            RecentlyWatchedEpisode(
                showId = 2,
                showTmdbId = 2,
                showTitle = "Severance",
                posterPath = "/2.jpg",
                seasonNumber = 1,
                episodeNumber = 1,
                episodeTitle = "Good News About Hell",
                watchedAt = 1_000L,
            ),
        )
    }
}
