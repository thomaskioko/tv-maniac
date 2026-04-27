package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class FakeTraktAuthManager : TraktAuthManager {

    private var onLaunchWebView: () -> Unit = { }

    public fun setOnLaunchWebView(onLaunch: () -> Unit) {
        onLaunchWebView = onLaunch
    }

    override fun launchWebView() {
        onLaunchWebView()
    }

    override fun registerResult() {
    }

    override fun setAuthCallback(callback: () -> Unit) {
    }
}
