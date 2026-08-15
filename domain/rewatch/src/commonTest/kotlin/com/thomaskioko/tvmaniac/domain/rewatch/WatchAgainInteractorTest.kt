package com.thomaskioko.tvmaniac.domain.rewatch

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class WatchAgainInteractorTest {

    private val rewatchRepository = FakeRewatchRepository()

    private val interactor = WatchAgainInteractor(rewatchRepository = rewatchRepository)

    @Test
    fun `should emit success given an episode is watched again`() = runTest {
        val param = episodeWatch(watchedAt = FIRST_WATCH)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should start a session given no session is open for the show`() = runTest {
        interactor.executeSync(episodeWatch(watchedAt = FIRST_WATCH))

        val openSession = rewatchRepository.openSessionForShow(SHOW_ID)
        openSession?.startedAt shouldBe FIRST_WATCH
        rewatchRepository.lastAddEpisodeSessionId shouldBe openSession?.id
    }

    @Test
    fun `should join the session already open given one exists for the show`() = runTest {
        val existingSessionId = rewatchRepository.startSession(showId = SHOW_ID, startedAt = FIRST_WATCH)

        interactor.executeSync(episodeWatch(watchedAt = SECOND_WATCH))

        rewatchRepository.lastAddEpisodeSessionId shouldBe existingSessionId
    }

    @Test
    fun `should join one session given the same episode is watched again before the session is closed`() = runTest {
        interactor.executeSync(episodeWatch(watchedAt = FIRST_WATCH))
        val firstSessionId = rewatchRepository.openSessionForShow(SHOW_ID)?.id

        interactor.executeSync(episodeWatch(watchedAt = MUCH_LATER_WATCH))

        rewatchRepository.observeSessionsForShow(SHOW_ID).test {
            awaitItem().size shouldBe 1
            cancelAndConsumeRemainingEvents()
        }
        rewatchRepository.lastAddEpisodeSessionId shouldBe firstSessionId
    }

    private fun episodeWatch(watchedAt: Long): WatchAgainInteractor.Param =
        WatchAgainInteractor.Param(
            showId = SHOW_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 3L,
            watchedAt = watchedAt,
        )

    private companion object {
        private const val SHOW_ID = 84958L
        private const val EPISODE_ID = 200L
        private const val FIRST_WATCH = 1_700_000_000_000L
        private const val SECOND_WATCH = 1_700_100_000_000L
        private const val MUCH_LATER_WATCH = FIRST_WATCH + 315_360_000_000L
    }
}
