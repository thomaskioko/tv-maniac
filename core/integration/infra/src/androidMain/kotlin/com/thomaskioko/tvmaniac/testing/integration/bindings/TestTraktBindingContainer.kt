package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.trakt.service.implementation.TraktPlatformBindingContainer
import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine

@BindingContainer
@ContributesTo(AppScope::class, replaces = [TraktPlatformBindingContainer::class])
public object TestTraktBindingContainer {

    public val handler: MockEngineHandler = MockEngineHandler.handler

    @Provides
    @SingleIn(AppScope::class)
    @TraktApi
    public fun provideTraktEngine(): HttpClientEngine = MockEngine { request ->
        handler.handle(this, request, printLogs = false)
    }
}
