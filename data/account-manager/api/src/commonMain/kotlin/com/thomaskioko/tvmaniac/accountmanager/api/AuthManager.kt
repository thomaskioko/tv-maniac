package com.thomaskioko.tvmaniac.accountmanager.api

/**
 * Drives the sign-in web flow for a single [SyncProviderSource]: launching it, registering the platform
 * result handler, and wiring the platform completion callback. Each backend contributes one
 * implementation into a multibound set; the composition root iterates the set to wire every provider.
 */
public interface AuthManager : SyncProvider {
    public fun launchWebView()

    public fun registerResult()

    public fun setAuthCallback(callback: () -> Unit)
}
