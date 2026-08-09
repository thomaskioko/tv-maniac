package com.thomaskioko.tvmaniac.domain.rewatch

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchRepository
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class FinishRewatchSessionInteractorTest {

    private val rewatchRepository = FakeRewatchRepository()

    private val interactor = FinishRewatchSessionInteractor(
        rewatchRepository = rewatchRepository,
    )

    @Test
    fun `should emit success given a rewatch session is finished`() = runTest {
        val sessionId = rewatchRepository.startSession(showId = SHOW_ID, startedAt = STARTED_AT)
        val param = FinishRewatchSessionInteractor.Param(sessionId = sessionId, closedAt = CLOSED_AT)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should leave no open session for the show given the only session is finished`() = runTest {
        val sessionId = rewatchRepository.startSession(showId = SHOW_ID, startedAt = STARTED_AT)

        interactor.executeSync(FinishRewatchSessionInteractor.Param(sessionId = sessionId, closedAt = CLOSED_AT))

        rewatchRepository.openSessionForShow(SHOW_ID).shouldBeNull()
    }

    private companion object {
        private const val SHOW_ID = 84958L
        private const val STARTED_AT = 1_700_000_000_000L
        private const val CLOSED_AT = 1_760_000_000_000L
    }
}
