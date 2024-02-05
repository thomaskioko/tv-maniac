package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import me.tatarka.inject.annotations.Inject

@Inject
actual class DefaultTraktAuthManager : TraktAuthManager {
  override fun launchWebView() {
    // NO OP
  }

  override fun registerResult() {
    // NO OP
  }
}
