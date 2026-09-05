package com.thomaskioko.tvmaniac.syncstate

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class DefaultSyncObserverTest {

    private val logger = FakeLogger()
    private val underTest = DefaultSyncObserver(logger)

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

    @Test
    fun `should emit BackgroundSyncFailed given block throws non-cancellation exception`() = runTest {
        val cause = IllegalStateException("rate limit 429")

        underTest.errors.test {
            assertFailsWith<IllegalStateException> {
                underTest.trackSync("library-sync") { throw cause }
            }

            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.BackgroundSyncFailed>()
            event.operationId shouldBe "library-sync"
            event.cause shouldBe cause
        }

        underTest.isSyncing.value shouldBe false
    }

    @Test
    fun `should record a logged error with the sync key given a mark watched failure`() = runTest {
        val cause = IllegalStateException("boom")

        underTest.log(SyncError.MarkWatchedFailed(showId = 1L, cause = cause))

        logger.recordedErrors shouldHaveSize 1
        logger.recordedErrors.first().throwable shouldBe cause
        logger.recordedErrors.first().keys shouldBe mapOf(CrashReportKeys.SYNC to "mark-watched")
    }

    @Test
    fun `should write a breadcrumb only given an account limit error`() = runTest {
        underTest.log(SyncError.AccountLimitExceeded(message = "limit", cause = IllegalStateException("limit")))

        logger.recordedErrors.shouldBeEmpty()
        logger.recordedWarnings shouldHaveSize 1
        logger.recordedWarnings.first().keys shouldBe mapOf(CrashReportKeys.SYNC to "account-limit")
    }

    @Test
    fun `should write a breadcrumb only given a tracked sync throws`() = runTest {
        assertFailsWith<IllegalStateException> {
            underTest.trackSync("library-sync") { throw IllegalStateException("boom") }
        }

        logger.recordedErrors.shouldBeEmpty()
        logger.recordedWarnings shouldHaveSize 1
        logger.recordedWarnings.first().keys shouldBe mapOf(CrashReportKeys.SYNC to "library-sync")
    }

    @Test
    fun `should not emit BackgroundSyncFailed given block is cancelled`() = runTest {
        underTest.errors.test {
            val deferred = async {
                underTest.trackSync("op-1") { CompletableDeferred<Unit>().await() }
            }
            runCurrent()

            deferred.cancel()
            deferred.join()

            expectNoEvents()
        }

        underTest.isSyncing.value shouldBe false
    }
}
