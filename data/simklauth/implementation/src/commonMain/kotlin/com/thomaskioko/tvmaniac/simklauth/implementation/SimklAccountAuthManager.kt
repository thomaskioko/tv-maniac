package com.thomaskioko.tvmaniac.simklauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.accountmanager.api.AuthManager
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.oauth.api.OAuthLauncher
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesIntoSet(ActivityScope::class)
public class SimklAccountAuthManager(
    private val launcher: OAuthLauncher,
    authClientConfigs: Set<AuthClientConfig>,
) : AuthManager {

    override val provider: AccountProvider = AccountProvider.SIMKL

    private val config: AuthClientConfig = authClientConfigs.first { it.provider == AccountProvider.SIMKL }

    override fun launchWebView() {
        launcher.launch(config)
    }

    override fun registerResult() {
        launcher.register()
    }

    override fun setAuthCallback(callback: () -> Unit) {
        launcher.setCallback(AccountProvider.SIMKL, callback)
    }
}
