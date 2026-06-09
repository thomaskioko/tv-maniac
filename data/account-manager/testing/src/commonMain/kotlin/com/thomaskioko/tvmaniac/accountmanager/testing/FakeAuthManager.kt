package com.thomaskioko.tvmaniac.accountmanager.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthManager

public class FakeAuthManager(
    override val provider: AccountProvider = AccountProvider.TRAKT,
) : AuthManager {

    private var onLaunchWebView: () -> Unit = { }

    public fun setOnLaunchWebView(onLaunch: () -> Unit) {
        onLaunchWebView = onLaunch
    }

    override fun launchWebView() {
        onLaunchWebView()
    }

    override fun registerResult() {
    }

    override fun setAuthCallback(callback: () -> Unit) {
    }
}
