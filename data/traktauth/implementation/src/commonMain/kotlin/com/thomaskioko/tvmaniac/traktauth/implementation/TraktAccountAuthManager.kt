package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.accountmanager.api.AuthManager
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.oauth.api.OAuthLauncher
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesIntoSet(ActivityScope::class)
public class TraktAccountAuthManager(
    private val launcher: OAuthLauncher,
    authClientConfigs: Set<AuthClientConfig>,
) : AuthManager {

    override val provider: AccountProvider = AccountProvider.TRAKT

    private val config: AuthClientConfig = authClientConfigs.first { it.provider == AccountProvider.TRAKT }

    override fun launchWebView() {
        launcher.launch(config)
    }

    override fun registerResult() {
        launcher.register()
    }

    override fun setAuthCallback(callback: () -> Unit) {
        launcher.setCallback(AccountProvider.TRAKT, callback)
    }
}
