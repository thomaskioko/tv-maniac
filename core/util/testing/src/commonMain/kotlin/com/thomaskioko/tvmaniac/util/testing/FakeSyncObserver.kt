package com.thomaskioko.tvmaniac.util.testing

import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
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

public class FakeSyncObserver : SyncObserver {

    private val mutex = Mutex()
    private var activeCount: Int = 0
    private val mutableIsSyncing = MutableStateFlow(false)
    private val mutableSyncStarted = MutableSharedFlow<Unit>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val mutableErrors = MutableSharedFlow<SyncError>(
        extraBufferCapacity = 16,
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
            log(SyncError.BackgroundSyncFailed(operationId, throwable))
            throw throwable
        } finally {
            decrement()
        }
    }

    override fun log(error: SyncError) {
        mutableErrors.tryEmit(error)
    }

    public fun setSyncing(value: Boolean) {
        mutableIsSyncing.value = value
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
}
