package com.thomaskioko.tvmaniac.tmdb.implementation

import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
public interface TmdbPlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideTmdbHttpClientEngine(): TmdbHttpClientEngine = OkHttp.create()
}
