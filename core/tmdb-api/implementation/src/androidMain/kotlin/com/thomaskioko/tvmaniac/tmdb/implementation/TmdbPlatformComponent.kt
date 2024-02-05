package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Provides

actual interface TmdbPlatformComponent {

  @ApplicationScope
  @Provides
  fun provideTmdbHttpClientEngine(): TmdbHttpClientEngine = OkHttp.create()
}
