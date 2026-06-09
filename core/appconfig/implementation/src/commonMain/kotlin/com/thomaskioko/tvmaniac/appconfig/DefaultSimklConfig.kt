package com.thomaskioko.tvmaniac.appconfig

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSimklConfig : SimklConfig {
    override val clientId: String = BuildConfig.SIMKL_CLIENT_ID
    override val clientSecret: String = BuildConfig.SIMKL_CLIENT_SECRET
    override val redirectUri: String = BuildConfig.SIMKL_REDIRECT_URI
}
