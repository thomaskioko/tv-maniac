package com.thomaskioko.tvmaniac.syncstate

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class DefaultSyncObserverTest {

    private val underTest = DefaultSyncObserver()

    @Test
    fun `should update isSyncing during the block and false after given single op`() = runTest {
        underTest.isSyncing.value shouldBe false

        val gate = CompletableDeferred<Unit>()
        val job = launch { underTest.trackSync("op-1") { gate.await() } }
        runCurrent()

        underTest.isSyncing.value shouldBe true

        gate.complete(Unit)
        job.join()

        underTest.isSyncing.value shouldBe false
    }

    @Test
    fun `should keep isSyncing true while either op runs given two concurrent ops with distinct ids`() = runTest {
        val firstGate = CompletableDeferred<Unit>()
        val secondGate = CompletableDeferred<Unit>()

        val first = launch { underTest.trackSync("op-1") { firstGate.await() } }
        val second = launch { underTest.trackSync("op-2") { secondGate.await() } }
        runCurrent()

        underTest.isSyncing.value shouldBe true

        firstGate.complete(Unit)
        first.join()

        underTest.isSyncing.value shouldBe true

        secondGate.complete(Unit)
        second.join()

        underTest.isSyncing.value shouldBe false
    }

    @Test
    fun `should treat concurrent ops with the same operationId as independent given counter semantics`() = runTest {
        val firstGate = CompletableDeferred<Unit>()
        val secondGate = CompletableDeferred<Unit>()

        val first = launch { underTest.trackSync("op-shared") { firstGate.await() } }
        val second = launch { underTest.trackSync("op-shared") { secondGate.await() } }
        runCurrent()

        underTest.isSyncing.value shouldBe true

        firstGate.complete(Unit)
        first.join()

        underTest.isSyncing.value shouldBe true

        secondGate.complete(Unit)
        second.join()

        underTest.isSyncing.value shouldBe false
    }

    @Test
    fun `should decrement on exception given block throws`() = runTest {
        assertFailsWith<IllegalStateException> {
            underTest.trackSync("op-1") { throw IllegalStateException("boom") }
        }

        underTest.isSyncing.value shouldBe false
    }

    @Test
    fun `should decrement on cancellation given block is cancelled`() = runTest {
        val deferred = async { underTest.trackSync("op-1") { CompletableDeferred<Unit>().await() } }
        runCurrent()

        underTest.isSyncing.value shouldBe true

        deferred.cancel()
        deferred.join()

        underTest.isSyncing.value shouldBe false
    }
}
