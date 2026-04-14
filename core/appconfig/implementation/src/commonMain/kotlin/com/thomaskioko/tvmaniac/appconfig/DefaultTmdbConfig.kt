package com.thomaskioko.tvmaniac.appconfig

import com.thomaskioko.tvmaniac.tmdb.api.TmdbConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTmdbConfig : TmdbConfig {
    override val apiKey: String = BuildConfig.TMDB_API_KEY
}
