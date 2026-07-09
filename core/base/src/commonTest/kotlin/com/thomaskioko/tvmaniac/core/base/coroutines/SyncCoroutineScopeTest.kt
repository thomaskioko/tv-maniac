package com.thomaskioko.tvmaniac.core.base.coroutines

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncCoroutineScopeTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val syncCoroutineScope = SyncCoroutineScope(dispatchers)

    @Test
    fun `should cancel active children given cancelActiveWork called`() = runTest(testDispatcher) {
        val job = syncCoroutineScope.scope.launch { awaitCancellation() }
        runCurrent()
        job.isActive shouldBe true

        syncCoroutineScope.cancelActiveWork()

        job.isCancelled shouldBe true
    }

    @Test
    fun `should keep scope usable given cancelActiveWork called`() = runTest(testDispatcher) {
        syncCoroutineScope.scope.launch { awaitCancellation() }
        runCurrent()

        syncCoroutineScope.cancelActiveWork()

        var ran = false
        syncCoroutineScope.scope.launch { ran = true }
        runCurrent()

        ran shouldBe true
    }
}
