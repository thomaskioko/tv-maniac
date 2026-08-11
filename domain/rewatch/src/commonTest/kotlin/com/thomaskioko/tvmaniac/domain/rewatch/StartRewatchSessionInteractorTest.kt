package com.thomaskioko.tvmaniac.domain.rewatch

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchRepository
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class StartRewatchSessionInteractorTest {

    private val rewatchRepository = FakeRewatchRepository()

    private val interactor = StartRewatchSessionInteractor(
        rewatchRepository = rewatchRepository,
    )

    @Test
    fun `should emit success given a rewatch session is started`() = runTest {
        val param = StartRewatchSessionInteractor.Param(showId = SHOW_ID, startedAt = STARTED_AT)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should open a session for the show given a rewatch is started`() = runTest {
        val param = StartRewatchSessionInteractor.Param(showId = SHOW_ID, startedAt = STARTED_AT)

        interactor.executeSync(param)

        rewatchRepository.openSessionForShow(SHOW_ID).shouldNotBeNull()
    }

    private companion object {
        private const val SHOW_ID = 84958L
        private const val STARTED_AT = 1_700_000_000_000L
    }
}
