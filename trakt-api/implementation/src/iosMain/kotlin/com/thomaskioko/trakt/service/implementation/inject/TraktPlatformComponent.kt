package com.thomaskioko.trakt.service.implementation.inject

import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
actual interface TraktPlatformComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideTraktHttpClientEngine(): TraktHttpClientEngine = Darwin.create()
}
