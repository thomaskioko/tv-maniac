package com.thomaskioko.tvmaniac.tmdb.implementation

import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface TmdbPlatformComponent {

  @Provides
  fun provideTmdbHttpClientEngine(): TmdbHttpClientEngine = Darwin.create()
}
