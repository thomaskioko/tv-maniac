package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.base.TmdbApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

@BindingContainer
@ContributesTo(AppScope::class)
public object TmdbPlatformBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    @TmdbApi
    public fun provideTmdbHttpClientEngine(): HttpClientEngine = Darwin.create()
}
