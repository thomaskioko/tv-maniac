package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.appconfig.DefaultTmdbConfig
import com.thomaskioko.tvmaniac.appconfig.DefaultTraktConfig
import com.thomaskioko.tvmaniac.tmdb.api.TmdbConfig
import com.thomaskioko.tvmaniac.trakt.api.TraktConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [
        DefaultTmdbConfig::class,
        DefaultTraktConfig::class,
    ],
)
public object FakeAppConfigBindings {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideTmdbConfig(): TmdbConfig = object : TmdbConfig {
        override val apiKey: String = "fake-tmdb-api-key"
    }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideTraktConfig(): TraktConfig = object : TraktConfig {
        override val clientId: String = "fake-trakt-client-id"
        override val clientSecret: String = "fake-trakt-client-secret"
        override val redirectUri: String = "tvmaniac://auth"
    }
}
