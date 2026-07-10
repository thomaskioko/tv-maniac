package com.thomaskioko.tvmaniac.simklauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProviderKey
import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.accountmanager.api.AuthManager
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.oauth.api.OAuthLauncher
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

@SingleIn(ActivityScope::class)
@ContributesIntoMap(
    scope = ActivityScope::class,
    binding = binding<
        @AccountProviderKey(SyncProviderSource.SIMKL)
        AuthManager,
        >(),
)
public class SimklAccountAuthManager(
    private val launcher: OAuthLauncher,
    authClientConfigs: Map<SyncProviderSource, AuthClientConfig>,
) : AuthManager {

    override val provider: SyncProviderSource = SyncProviderSource.SIMKL

    private val config: AuthClientConfig = authClientConfigs.getValue(SyncProviderSource.SIMKL)

    override fun launchWebView() {
        launcher.launch(config)
    }

    override fun registerResult() {
        launcher.register()
    }

    override fun setAuthCallback(callback: () -> Unit) {
        launcher.setCallback(SyncProviderSource.SIMKL, callback)
    }
}
