package com.thomaskioko.tvmaniac.domain.syncactivity

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncActivityInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val activityRepository = FakeTraktActivityRepository()

    private val interactor = SyncActivityInteractor(
        traktActivityRepository = activityRepository,
        dispatchers = dispatchers,
    )

    @Test
    fun `should invoke fetchLatestActivities with the provided forceRefresh flag`() = runTest(testDispatcher) {
        interactor.executeSync(SyncActivityInteractor.Param(forceRefresh = false))
        interactor.executeSync(SyncActivityInteractor.Param(forceRefresh = true))

        activityRepository.fetchInvocations() shouldBe listOf(false, true)
    }

    @Test
    fun `should default forceRefresh to false when Param is unspecified`() = runTest(testDispatcher) {
        interactor.executeSync(SyncActivityInteractor.Param())

        activityRepository.fetchInvocations() shouldBe listOf(false)
    }
}
