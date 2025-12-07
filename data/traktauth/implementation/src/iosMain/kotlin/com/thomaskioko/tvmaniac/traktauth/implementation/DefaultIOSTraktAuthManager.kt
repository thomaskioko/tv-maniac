package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.concurrent.Volatile

@Inject
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
