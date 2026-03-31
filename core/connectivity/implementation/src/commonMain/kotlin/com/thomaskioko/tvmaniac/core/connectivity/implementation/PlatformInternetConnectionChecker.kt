package com.thomaskioko.tvmaniac.core.connectivity.implementation

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker

public expect class PlatformInternetConnectionChecker : InternetConnectionChecker {
    override fun isConnected(): Boolean
}
