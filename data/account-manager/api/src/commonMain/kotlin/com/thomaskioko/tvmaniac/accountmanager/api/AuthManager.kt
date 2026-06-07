package com.thomaskioko.tvmaniac.accountmanager.api

/**
 * Launches the sign-in web flow for a single [AccountProvider]. Each backend contributes one
 * implementation into a multibound set; consumers launch the entry for the provider being connected.
 */
public interface AuthManager : ProviderScoped {
    public fun launchWebView()
}
