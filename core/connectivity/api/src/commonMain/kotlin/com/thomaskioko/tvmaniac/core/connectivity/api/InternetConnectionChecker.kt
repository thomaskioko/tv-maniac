package com.thomaskioko.tvmaniac.core.connectivity.api

import kotlinx.coroutines.flow.Flow

public interface InternetConnectionChecker {
    public fun isConnected(): Boolean

    public fun observeConnection(): Flow<Boolean>
}
