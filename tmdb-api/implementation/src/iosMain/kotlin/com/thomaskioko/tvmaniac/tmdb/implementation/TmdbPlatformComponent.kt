package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides

actual interface TmdbPlatformComponent {

  @ApplicationScope
  @Provides
  fun provideTmdbHttpClientEngine(): TmdbHttpClientEngine = Darwin.create()
}
