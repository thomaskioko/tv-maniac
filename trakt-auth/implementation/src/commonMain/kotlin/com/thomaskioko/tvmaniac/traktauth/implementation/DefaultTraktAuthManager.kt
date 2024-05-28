package com.thomaskioko.tvmaniac.traktauth.implementation

import me.tatarka.inject.annotations.Inject

@Inject
expect class DefaultTraktAuthManager {
  fun launchWebView()

  fun registerResult()
}
