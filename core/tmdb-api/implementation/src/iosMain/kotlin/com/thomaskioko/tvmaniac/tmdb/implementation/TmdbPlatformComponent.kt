package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides

interface TmdbPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideTmdbHttpClientEngine(): TmdbHttpClientEngine = Darwin.create()
}
