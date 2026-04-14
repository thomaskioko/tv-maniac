package com.thomaskioko.tvmaniac.appconfig

import com.thomaskioko.tvmaniac.trakt.api.TraktConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktConfig : TraktConfig {
    override val clientId: String = BuildConfig.TRAKT_CLIENT_ID
    override val clientSecret: String = BuildConfig.TRAKT_CLIENT_SECRET
    override val redirectUri: String = BuildConfig.TRAKT_REDIRECT_URI
}
