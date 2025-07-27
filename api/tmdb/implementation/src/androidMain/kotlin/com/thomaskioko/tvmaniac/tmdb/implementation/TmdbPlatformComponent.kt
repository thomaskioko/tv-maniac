package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.implementation.di.TmdbHttpClientEngine
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.engine.okhttp.OkHttp

@ContributesTo(AppScope::class)
interface TmdbPlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideTmdbHttpClientEngine(): TmdbHttpClientEngine = OkHttp.create()
}
