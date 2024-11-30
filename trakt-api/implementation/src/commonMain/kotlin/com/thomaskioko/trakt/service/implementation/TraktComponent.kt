package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

typealias TraktHttpClient = HttpClient
typealias TraktHttpClientEngine = HttpClientEngine
typealias TraktJson = Json

@ContributesTo(AppScope::class)
interface TraktComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideJson(): TraktJson = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideHttpClient(
    configs: Configs,
    json: TraktJson,
    httpClientEngine: TraktHttpClientEngine,
    logger: KermitLogger,
  ): TraktHttpClient =
    traktHttpClient(
      isDebug = configs.isDebug,
      traktClientId = configs.traktClientId,
      json = json,
      httpClientEngine = httpClientEngine,
      kermitLogger = logger,
    )
}
