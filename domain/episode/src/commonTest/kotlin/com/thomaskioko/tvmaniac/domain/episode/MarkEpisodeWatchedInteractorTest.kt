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
    }

    @Test
    fun `should complete successfully when marking episode from different season`() = runTest {
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
    }
}
