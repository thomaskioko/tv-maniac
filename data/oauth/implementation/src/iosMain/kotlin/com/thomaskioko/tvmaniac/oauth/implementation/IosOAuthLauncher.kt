package com.thomaskioko.tvmaniac.oauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.oauth.api.OAuthLauncher
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosOAuthLauncher : OAuthLauncher {

    private val callbacks: MutableMap<AccountProvider, () -> Unit> = mutableMapOf()

    override fun register() {
    }

    override fun launch(config: AuthClientConfig) {
        callbacks[config.provider]?.invoke()
    }

    override fun setCallback(provider: AccountProvider, callback: () -> Unit) {
        callbacks[provider] = callback
    }
}
