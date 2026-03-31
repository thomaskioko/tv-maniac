package com.thomaskioko.tvmaniac.core.connectivity.testing

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker

public class FakeInternetConnectionChecker(
    private val connected: Boolean = true,
) : InternetConnectionChecker {

    override fun isConnected(): Boolean = connected
}
