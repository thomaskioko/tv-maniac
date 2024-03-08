package com.thomaskioko.trakt.service.implementation.inject

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides

actual interface TraktPlatformComponent {

  @ApplicationScope
  @Provides
  fun provideTraktHttpClientEngine(): TraktHttpClientEngine = Darwin.create()
}
