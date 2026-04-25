package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.base.TmdbApi
import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbPlatformBindingContainer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine

@BindingContainer
@ContributesTo(AppScope::class, replaces = [TmdbPlatformBindingContainer::class])
public object TestTmdbBindingContainer {

    public val handler: MockEngineHandler = MockEngineHandler()

    @Provides
    @SingleIn(AppScope::class)
    @TmdbApi
    public fun provideTmdbEngine(): HttpClientEngine = MockEngine { request ->
        handler.handle(this, request, printLogs = false)
    }
}
