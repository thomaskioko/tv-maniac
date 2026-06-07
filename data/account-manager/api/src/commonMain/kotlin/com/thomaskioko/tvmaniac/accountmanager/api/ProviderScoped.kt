package com.thomaskioko.tvmaniac.accountmanager.api

/**
 * Remote data source bound to a single [AccountProvider].
 *
 * Each backend contributes an implementation into a multibound set; consumers pick the one serving the
 * active account with [getActiveProvider].
 */
public interface ProviderScoped {
    public val provider: AccountProvider
}

/**
 * Returns the source serving the active account, or null when no provider is connected or none serves it.
 */
public fun <T : ProviderScoped> Set<T>.getActiveProvider(
    accountManager: AccountManager,
): T? = firstOrNull { it.provider == accountManager.getActiveProvider() }
