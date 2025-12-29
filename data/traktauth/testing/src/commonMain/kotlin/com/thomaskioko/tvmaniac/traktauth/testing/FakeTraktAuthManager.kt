package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager

public class FakeTraktAuthManager : TraktAuthManager {
    override fun launchWebView() {
    }

    override fun registerResult() {
    }

    override fun setAuthCallback(callback: () -> Unit) {
    }
}
