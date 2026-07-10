package com.thomaskioko.tvmaniac.domain.settings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ObserveLayoutPreferencesInteractorTest {

    private val datastoreRepository = FakeDatastoreRepository()
    private val interactor = ObserveLayoutPreferencesInteractor(datastoreRepository)

    @Test
    fun `should emit haptic feedback enabled by default`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().hapticFeedbackEnabled shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit saved haptic feedback value given the preference changes`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().hapticFeedbackEnabled shouldBe true

            datastoreRepository.saveHapticFeedbackEnabled(false)

            awaitItem().hapticFeedbackEnabled shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }
}
