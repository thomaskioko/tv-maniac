package com.thomaskioko.tvmaniac.core.base.interactor

import com.thomaskioko.tvmaniac.core.view.InvokeError
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeStatus
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.minutes

// https://github.com/chrisbanes/tivi/blob/main/domain/src/commonMain/kotlin/app/tivi/domain/Interactor.kt
public abstract class Interactor<in P> {
    public operator fun invoke(
        params: P,
        timeoutMs: Long = defaultTimeoutMs,
    ): Flow<InvokeStatus> = flow {
        try {
            withTimeout(timeoutMs) {
                emit(InvokeStarted)
                doWork(params)
                emit(InvokeSuccess)
            }
        } catch (t: TimeoutCancellationException) {
            emit(InvokeError(t))
        }
    }.catch { t -> emit(InvokeError(t)) }

    public suspend fun executeSync(params: P): Unit = doWork(params)

    protected abstract suspend fun doWork(params: P)

    public companion object {
        private val defaultTimeoutMs = 5.minutes.inWholeMilliseconds
    }
}

public suspend inline fun Interactor<Unit>.executeSync(): Unit = executeSync(Unit)

public abstract class SubjectInteractor<P : Any, T> {
    // Ideally this would be buffer = 0, since we use flatMapLatest below, BUT invoke is not
    // suspending. This means that we can't suspend while flatMapLatest cancels any
    // existing flows. The buffer of 1 means that we can use tryEmit() and buffer the value
    // instead, resulting in mostly the same result.
    private val paramState = MutableSharedFlow<P>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    public val flow: Flow<T> = paramState
        .distinctUntilChanged()
        .flatMapLatest { createObservable(it) }
        .distinctUntilChanged()

    public operator fun invoke(params: P) {
        paramState.tryEmit(params)
    }

    protected abstract fun createObservable(params: P): Flow<T>
}
