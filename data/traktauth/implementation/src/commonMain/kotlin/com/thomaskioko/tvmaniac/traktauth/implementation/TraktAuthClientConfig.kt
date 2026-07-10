package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProviderKey
import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.trakt.api.TraktConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

@SingleIn(AppScope::class)
@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<
        @AccountProviderKey(SyncProviderSource.TRAKT)
        AuthClientConfig,
        >(),
)
public class TraktAuthClientConfig(
    traktConfig: TraktConfig,
) : AuthClientConfig {

    override val provider: SyncProviderSource = SyncProviderSource.TRAKT
    override val clientId: String = traktConfig.clientId
    override val clientSecret: String = traktConfig.clientSecret
    override val redirectUri: String = traktConfig.redirectUri
    override val authorizationEndpoint: String = "https://trakt.tv/oauth/authorize"
    override val tokenEndpoint: String = "https://api.trakt.tv/oauth/token"
    override val scopes: List<String> = emptyList()
}
