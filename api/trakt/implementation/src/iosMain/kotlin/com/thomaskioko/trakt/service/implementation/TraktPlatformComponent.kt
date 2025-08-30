package com.thomaskioko.trakt.service.implementation

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.ktor.client.engine.darwin.Darwin

@ContributesTo(AppScope::class)
interface TraktPlatformComponent {

    @Provides
    fun provideTraktHttpClientEngine(): TraktHttpClientEngine = Darwin.create()
}
