package com.thomaskioko.trakt.service.implementation.inject

import com.thomaskioko.trakt.service.implementation.TraktListRemoteDataSourceImpl
import com.thomaskioko.trakt.service.implementation.TraktTokenRemoteDataSourceImpl
import com.thomaskioko.trakt.service.implementation.TraktUserRemoteDataSourceImpl
import com.thomaskioko.trakt.service.implementation.traktHttpClient
import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktTokenRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

typealias TraktHttpClient = HttpClient

typealias TraktJson = Json

@ContributesTo(AppScope::class)
interface TraktComponent : TraktPlatformComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideJson(): TraktJson = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
  }

  @SingleIn(AppScope::class)
  @Provides
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

  @SingleIn(AppScope::class)
  @Provides
  fun provideTraktListRemoteDataSource(
    bind: TraktListRemoteDataSourceImpl,
  ): TraktListRemoteDataSource = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideTraktTokenRemoteDataSource(
    bind: TraktTokenRemoteDataSourceImpl,
  ): TraktTokenRemoteDataSource = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideTraktUserRemoteDataSource(
    bind: TraktUserRemoteDataSourceImpl,
  ): TraktUserRemoteDataSource = bind
}
