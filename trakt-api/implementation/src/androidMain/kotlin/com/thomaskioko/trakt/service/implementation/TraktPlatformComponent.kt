package com.thomaskioko.trakt.service.implementation

import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface TraktPlatformComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideTraktHttpClientEngine(interceptor: TraktAuthInterceptor): TraktHttpClientEngine =
    OkHttp.create { addInterceptor(interceptor) }
}
