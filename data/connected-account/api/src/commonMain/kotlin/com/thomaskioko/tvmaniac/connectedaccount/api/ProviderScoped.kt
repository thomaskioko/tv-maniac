package com.thomaskioko.tvmaniac.connectedaccount.api

/**
 * Remote data source bound to a single [ConnectedProvider].
 *
 * Each backend contributes an implementation into a multibound set; consumers pick the one serving the
 * active account with [getActiveProvider].
 */
public interface ProviderScoped {
    public val provider: ConnectedProvider
}

/**
 * Returns the source serving the active account, or null when no provider is connected or none serves it.
 */
public fun <T : ProviderScoped> Set<T>.getActiveProvider(
    connectedAccountRepository: ConnectedAccountRepository,
): T? = firstOrNull { it.provider == connectedAccountRepository.getActiveProvider() }
