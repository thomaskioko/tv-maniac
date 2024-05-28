package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager

expect class DefaultTraktAuthManager : TraktAuthManager {

  override fun launchWebView()

  override fun registerResult()
}
