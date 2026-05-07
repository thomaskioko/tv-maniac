package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.util.api.SyncError
import com.thomaskioko.tvmaniac.util.api.SyncErrorChannel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSyncErrorChannel : SyncErrorChannel {
    private val mutableErrors = MutableSharedFlow<SyncError>(
        extraBufferCapacity = BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val errors: SharedFlow<SyncError> = mutableErrors.asSharedFlow()

    override suspend fun report(error: SyncError) {
        mutableErrors.emit(error)
    }

    private companion object {
        private const val BUFFER_CAPACITY = 16
    }
}
