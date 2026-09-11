package com.thomaskioko.tvmaniac.core.connectivity.implementation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal val RECONNECT_DEBOUNCE: Duration = 2.seconds

internal fun Flow<Boolean>.observeReconnections(): Flow<Unit> =
    debounce(RECONNECT_DEBOUNCE)
        .distinctUntilChanged()
        .drop(1)
        .filter { it }
        .map { }
