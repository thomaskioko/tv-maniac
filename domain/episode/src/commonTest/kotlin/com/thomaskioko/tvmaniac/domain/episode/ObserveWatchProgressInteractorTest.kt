package com.thomaskioko.tvmaniac.domain.episode

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObserveWatchProgressInteractorTest {
    private val episodeRepository = FakeEpisodeRepository()

    private val interactor = ObserveWatchProgressInteractor(
        episodeRepository = episodeRepository,
    )

    @Test
    fun `should return watch progress for show`() = runTest {
        val showId = 84958L
        val progress = WatchProgress(
            showId = showId,
            totalEpisodesWatched = 5,
            lastSeasonWatched = 1,
            lastEpisodeWatched = 5,
            nextEpisode = null,
        )

        episodeRepository.setWatchProgress(showId, progress)

        interactor(showId)

        interactor.flow.test {
            awaitItem() shouldBe progress
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should update when progress changes`() = runTest {
        val showId = 84958L
        val initialProgress = WatchProgress(
            showId = showId,
            totalEpisodesWatched = 5,
            lastSeasonWatched = 1,
            lastEpisodeWatched = 5,
            nextEpisode = null,
        )

        val updatedProgress = WatchProgress(
            showId = showId,
            totalEpisodesWatched = 6,
            lastSeasonWatched = 1,
            lastEpisodeWatched = 6,
            nextEpisode = null,
        )

        episodeRepository.setWatchProgress(showId, initialProgress)

        interactor(showId)

        interactor.flow.test {
            awaitItem() shouldBe initialProgress

            episodeRepository.setWatchProgress(showId, updatedProgress)

            awaitItem() shouldBe updatedProgress
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return zero progress for new show`() = runTest {
        val showId = 84958L
        val emptyProgress = WatchProgress(
            showId = showId,
            totalEpisodesWatched = 0,
            lastSeasonWatched = null,
            lastEpisodeWatched = null,
            nextEpisode = null,
        )

        episodeRepository.setWatchProgress(showId, emptyProgress)

        interactor(showId)

        interactor.flow.test {
            awaitItem() shouldBe emptyProgress
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should handle 100 percent progress`() = runTest {
        val showId = 84958L
        val fullProgress = WatchProgress(
            showId = showId,
            totalEpisodesWatched = 12,
            lastSeasonWatched = 2,
            lastEpisodeWatched = 6,
            nextEpisode = null,
        )

        episodeRepository.setWatchProgress(showId, fullProgress)

        interactor(showId)

        interactor.flow.test {
            awaitItem() shouldBe fullProgress
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should handle different shows independently`() = runTest {
        val showId1 = 84958L
        val showId2 = 1232L

        val progress1 = WatchProgress(
            showId = showId1,
            totalEpisodesWatched = 5,
            lastSeasonWatched = 1,
            lastEpisodeWatched = 5,
            nextEpisode = null,
        )

        val progress2 = WatchProgress(
            showId = showId2,
            totalEpisodesWatched = 3,
            lastSeasonWatched = 1,
            lastEpisodeWatched = 3,
            nextEpisode = null,
        )

        episodeRepository.setWatchProgress(showId1, progress1)
        episodeRepository.setWatchProgress(showId2, progress2)

        val interactor1 = ObserveWatchProgressInteractor(episodeRepository)
        val interactor2 = ObserveWatchProgressInteractor(episodeRepository)

        interactor1(showId1)
        interactor2(showId2)

        interactor1.flow.test {
            awaitItem() shouldBe progress1
            cancelAndConsumeRemainingEvents()
        }

        interactor2.flow.test {
            awaitItem() shouldBe progress2
            cancelAndConsumeRemainingEvents()
        }
    }
}
