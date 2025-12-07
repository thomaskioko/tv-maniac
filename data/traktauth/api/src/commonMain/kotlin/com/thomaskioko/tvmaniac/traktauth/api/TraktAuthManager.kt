package com.thomaskioko.tvmaniac.traktauth.api

public interface TraktAuthManager {
    public fun launchWebView()

    public fun registerResult()

    public fun setAuthCallback(callback: () -> Unit)
}
