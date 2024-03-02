package com.thomaskioko.trakt.service.implementation.inject

import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides

actual interface TraktPlatformComponent {

  @ApplicationScope
  @Provides
  fun provideTraktHttpClientEngine(): TraktHttpClientEngine = Darwin.create()
}
