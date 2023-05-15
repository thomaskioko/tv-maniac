package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides

interface TraktPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideTraktHttpClientEngine(): TraktHttpClientEngine = Darwin.create()
}
