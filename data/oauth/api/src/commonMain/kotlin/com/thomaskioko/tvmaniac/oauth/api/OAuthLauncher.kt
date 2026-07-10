package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource

/**
 * Shared, provider-agnostic OAuth launcher. [launch] runs the authorization-code flow for the given
 * provider's [AuthClientConfig] and persists the result through [AuthStateHolder]; providers differ only by
 * config, not by behaviour. On Android it drives AppAuth; on iOS it bridges to the per-provider native
 * coordinator registered through [setCallback]. A single instance serves every provider — OAuth flows run
 * one at a time.
 */
public interface OAuthLauncher {
    public fun register()
    public fun launch(config: AuthClientConfig)
    public fun setCallback(provider: SyncProviderSource, callback: () -> Unit)
}
