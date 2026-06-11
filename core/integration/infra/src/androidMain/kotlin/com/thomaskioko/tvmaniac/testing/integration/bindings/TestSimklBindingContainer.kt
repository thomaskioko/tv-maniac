package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.core.base.SimklApi
import com.thomaskioko.tvmaniac.simkl.implementation.SimklPlatformBindingContainer
import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine

@BindingContainer
@ContributesTo(AppScope::class, replaces = [SimklPlatformBindingContainer::class])
public object TestSimklBindingContainer {

    public val handler: MockEngineHandler = MockEngineHandler.handler

    @Provides
    @SingleIn(AppScope::class)
    @SimklApi
    public fun provideSimklEngine(): HttpClientEngine = MockEngine { request ->
        handler.handle(this, request, printLogs = false)
    }
}
