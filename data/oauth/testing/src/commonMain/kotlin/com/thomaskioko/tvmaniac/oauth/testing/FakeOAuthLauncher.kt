package com.thomaskioko.tvmaniac.oauth.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.oauth.api.OAuthLauncher

public class FakeOAuthLauncher : OAuthLauncher {
    public var launchedConfig: AuthClientConfig? = null
        private set
    public var registerCount: Int = 0
        private set

    private var onLaunch: (AuthClientConfig) -> Unit = {}

    public fun setOnLaunch(onLaunch: (AuthClientConfig) -> Unit) {
        this.onLaunch = onLaunch
    }

    override fun register() {
        registerCount++
    }

    override fun launch(config: AuthClientConfig) {
        launchedConfig = config
        onLaunch(config)
    }

    override fun setCallback(provider: SyncProviderSource, callback: () -> Unit) {
    }
}
