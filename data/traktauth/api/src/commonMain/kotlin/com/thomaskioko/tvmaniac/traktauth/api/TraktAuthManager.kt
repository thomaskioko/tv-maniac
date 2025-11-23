package com.thomaskioko.tvmaniac.traktauth.api

interface TraktAuthManager {
    fun launchWebView()

    fun registerResult()

    fun setAuthCallback(callback: () -> Unit)
}
