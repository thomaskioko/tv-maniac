package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import me.tatarka.inject.annotations.Inject

// TODO:: Replace with actual typealias. See https://youtrack.jetbrains.com/issue/KT-61573
@Inject
actual class TraktAuthManagerImpl : TraktAuthManager {

    override fun launchWebView() {
        // NO OP
    }

    override fun registerResult() {
        // NO OP
    }
}
