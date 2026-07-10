package com.thomaskioko.tvmaniac.simklauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProviderKey
import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.appconfig.SimklConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

@SingleIn(AppScope::class)
@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<
        @AccountProviderKey(SyncProviderSource.SIMKL)
        AuthClientConfig,
        >(),
)
public class SimklAuthClientConfig(
    simklConfig: SimklConfig,
) : AuthClientConfig {

    override val provider: SyncProviderSource = SyncProviderSource.SIMKL
    override val clientId: String = simklConfig.clientId
    override val clientSecret: String = simklConfig.clientSecret
    override val redirectUri: String = simklConfig.redirectUri
    override val authorizationEndpoint: String = "https://simkl.com/oauth/authorize"
    override val tokenEndpoint: String = "https://api.simkl.com/oauth/token"
    override val scopes: List<String> = emptyList()
}
