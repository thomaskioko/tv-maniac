package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlin.concurrent.Volatile

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultIOSTraktAuthManager : TraktAuthManager {

    @Volatile
    private var authCallback: (() -> Unit)? = null

    override fun setAuthCallback(callback: () -> Unit) {
        this.authCallback = callback
    }

    override fun launchWebView() {
        authCallback?.invoke()
    }

    override fun registerResult() {
    }
}
