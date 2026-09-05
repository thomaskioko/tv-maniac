package com.thomaskioko.tvmaniac.syncstate

import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSyncObserver(
    private val logger: Logger,
) : SyncObserver {

    private val mutex = Mutex()
    private var activeCount: Int = 0
    private val mutableIsSyncing = MutableStateFlow(false)
    private val mutableSyncStarted = MutableSharedFlow<Unit>(
        extraBufferCapacity = BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val mutableErrors = MutableSharedFlow<SyncError>(
        extraBufferCapacity = BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val isSyncing: StateFlow<Boolean> = mutableIsSyncing.asStateFlow()

    override val syncStarted: SharedFlow<Unit> = mutableSyncStarted.asSharedFlow()

    override val errors: SharedFlow<SyncError> = mutableErrors.asSharedFlow()

    override suspend fun <T> trackSync(operationId: String, block: suspend () -> T): T {
        increment()
        return try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            logger.warning(LOG_TAG, "Sync [$operationId] failed", throwable, mapOf(CrashReportKeys.SYNC to operationId))
            mutableErrors.tryEmit(SyncError.BackgroundSyncFailed(operationId, throwable))
            throw throwable
        } finally {
            decrement()
        }
    }

    override fun log(error: SyncError) {
        report(error)
        mutableErrors.tryEmit(error)
    }

    private fun report(error: SyncError) {
        val keys = mapOf(CrashReportKeys.SYNC to error.operation)
        when (error) {
            is SyncError.AccountLimitExceeded -> logger.warning(LOG_TAG, "Account limit exceeded", error.cause, keys)
            else -> logger.error(LOG_TAG, "Sync [${error.operation}] failed", error.cause, keys)
        }
    }

    private val SyncError.operation: String
        get() = when (this) {
            is SyncError.BackgroundSyncFailed -> operationId
            is SyncError.MarkWatchedFailed -> "mark-watched"
            is SyncError.MarkUnwatchedFailed -> "mark-unwatched"
            is SyncError.BatchMarkFailed -> "batch-mark"
            is SyncError.AccountLimitExceeded -> "account-limit"
        }

    private suspend fun increment() {
        mutex.withLock {
            activeCount++
            if (activeCount == 1) mutableIsSyncing.value = true
        }
        mutableSyncStarted.tryEmit(Unit)
    }

    private suspend fun decrement() {
        mutex.withLock {
            activeCount--
            if (activeCount == 0) mutableIsSyncing.value = false
        }
    }

    private companion object {
        private const val BUFFER_CAPACITY = 16
        private const val LOG_TAG = "SyncObserver"
    }
}
