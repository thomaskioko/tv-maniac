package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.base.SimklApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

@BindingContainer
@ContributesTo(AppScope::class)
public object SimklPlatformBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    @SimklApi
    public fun provideSimklHttpClientEngine(): HttpClientEngine = OkHttp.create()
}
