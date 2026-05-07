package com.thomaskioko.tvmaniac.util.api

import kotlinx.coroutines.flow.SharedFlow

public interface SyncErrorChannel {
    public val errors: SharedFlow<SyncError>
    public suspend fun report(error: SyncError)
}
