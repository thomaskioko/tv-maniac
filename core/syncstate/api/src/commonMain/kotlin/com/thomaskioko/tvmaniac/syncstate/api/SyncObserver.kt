package com.thomaskioko.tvmaniac.syncstate.api

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

public interface SyncObserver {

    public val isSyncing: StateFlow<Boolean>

    public val syncStarted: SharedFlow<Unit>

    public val errors: SharedFlow<SyncError>

    public suspend fun <T> trackSync(operationId: String, block: suspend () -> T): T

    public fun log(error: SyncError)
}
