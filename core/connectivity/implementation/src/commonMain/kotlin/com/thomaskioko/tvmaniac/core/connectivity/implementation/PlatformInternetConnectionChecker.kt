package com.thomaskioko.tvmaniac.core.connectivity.implementation

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import kotlinx.coroutines.flow.Flow

public expect class PlatformInternetConnectionChecker : InternetConnectionChecker {
    override fun isConnected(): Boolean

    override fun observeConnection(): Flow<Boolean>
}
