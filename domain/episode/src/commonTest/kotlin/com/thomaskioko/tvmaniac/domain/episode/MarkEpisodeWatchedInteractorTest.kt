package com.thomaskioko.tvmaniac.domain.episode

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MarkEpisodeWatchedInteractorTest {
    private val episodeRepository = FakeEpisodeRepository()

    private val interactor = MarkEpisodeWatchedInteractor(
        episodeRepository = episodeRepository,
    )

    @Test
    fun `should mark episode as watched successfully`() = runTest {
        val params = MarkEpisodeWatchedParams(
            showId = 84958,
            episodeId = 123456,
            seasonNumber = 1,
            episodeNumber = 5,
        )

        interactor(params).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        episodeRepository.isEpisodeWatched(84958, 1, 5) shouldBe true
    }

    @Test
    fun `should handle multiple episodes being marked as watched`() = runTest {
        val episode1 = MarkEpisodeWatchedParams(
            showId = 84958,
            episodeId = 123456,
            seasonNumber = 1,
            episodeNumber = 1,
        )

        val episode2 = MarkEpisodeWatchedParams(
            showId = 84958,
            episodeId = 123457,
            seasonNumber = 1,
            episodeNumber = 2,
        )

        interactor(episode1).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        interactor(episode2).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        episodeRepository.isEpisodeWatched(84958, 1, 1) shouldBe true
        episodeRepository.isEpisodeWatched(84958, 1, 2) shouldBe true
    }

    @Test
    fun `should update last watched episode when marking as watched`() = runTest {
        val params = MarkEpisodeWatchedParams(
            showId = 84958,
            episodeId = 123456,
            seasonNumber = 2,
            episodeNumber = 3,
        )

        interactor(params).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        val lastWatched = episodeRepository.getLastWatchedEpisode(84958)
        lastWatched?.episode_id?.id shouldBe 123456
        lastWatched?.season_number shouldBe 2
        lastWatched?.episode_number shouldBe 3
    }

    @Test
    fun `should handle episodes from different shows`() = runTest {
        val lokiEpisode = MarkEpisodeWatchedParams(
            showId = 84958,
            episodeId = 123456,
            seasonNumber = 1,
            episodeNumber = 1,
        )

        val lazarusEpisode = MarkEpisodeWatchedParams(
            showId = 1232,
            episodeId = 789012,
            seasonNumber = 1,
            episodeNumber = 1,
        )

        interactor(lokiEpisode).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        interactor(lazarusEpisode).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        episodeRepository.isEpisodeWatched(84958, 1, 1) shouldBe true
        episodeRepository.isEpisodeWatched(1232, 1, 1) shouldBe true
    }
}
