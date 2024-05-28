package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import me.tatarka.inject.annotations.Inject

@Inject
actual class DefaultTraktAuthManager : TraktAuthManager {
  actual override fun launchWebView() {
    // NO OP
  }

  actual override fun registerResult() {
    // NO OP
  }
}
