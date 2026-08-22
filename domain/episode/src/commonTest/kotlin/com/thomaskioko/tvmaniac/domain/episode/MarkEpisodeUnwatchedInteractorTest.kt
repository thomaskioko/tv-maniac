package com.thomaskioko.tvmaniac.domain.episode

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class MarkEpisodeUnwatchedInteractorTest {
    private val episodeRepository = FakeEpisodeRepository()
    private val rewatchRepository = FakeRewatchRepository()

    private val interactor = MarkEpisodeUnwatchedInteractor(
        episodeRepository = episodeRepository,
        rewatchRepository = rewatchRepository,
    )

    @Test
    fun `should mark episode as unwatched given params are valid`() = runTest {
        val params = MarkEpisodeUnwatchedParams(showId = 84958, episodeId = 123456)

        interactor(params).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should clear recorded plays given the episode is unwatched`() = runTest {
        rewatchRepository.setRewatchesForEpisode(episodeId = 123456, rewatches = 2)

        val params = MarkEpisodeUnwatchedParams(showId = 84958, episodeId = 123456)

        interactor(params).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        rewatchRepository.observeEpisodeRewatches(123456).first() shouldBe 0L
    }

    @Test
    fun `should keep plays of other episodes given one episode is unwatched`() = runTest {
        rewatchRepository.setRewatchesForEpisode(episodeId = 123456, rewatches = 2)
        rewatchRepository.setRewatchesForEpisode(episodeId = 123457, rewatches = 3)

        interactor(MarkEpisodeUnwatchedParams(showId = 84958, episodeId = 123456)).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        rewatchRepository.observeEpisodeRewatches(123457).first() shouldBe 3L
    }
}
