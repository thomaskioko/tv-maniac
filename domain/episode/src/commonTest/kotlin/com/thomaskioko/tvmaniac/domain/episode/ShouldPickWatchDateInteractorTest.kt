package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ShouldPickWatchDateInteractorTest {
    private val datastoreRepository = FakeDatastoreRepository()

    private val interactor = ShouldPickWatchDateInteractor(
        datastoreRepository = datastoreRepository,
    )

    @Test
    fun `should not pick a date given nothing has been saved`() = runTest {
        interactor() shouldBe false
    }

    @Test
    fun `should pick a date given the setting is enabled`() = runTest {
        datastoreRepository.saveCustomWatchDateEnabled(true)

        interactor() shouldBe true
    }
}
