package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.base.model.AppConfig
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbNetworkComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalSerializationApi::class)
@ApplicationScope
@Inject
class TmdbAndroidNetworkComponent : TmdbNetworkComponent {

    override fun json(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    override fun httpClientEngine(): HttpClientEngine = OkHttp.create()

    override fun httpClient(
        appConfig: AppConfig,
        json: Json,
        httpClientEngine: HttpClientEngine
    ): HttpClient = tmdbHttpClient(
        isDebug = appConfig.isDebug,
        tmdbApiKey = appConfig.tmdbApiKey,
        json = json,
        httpClientEngine = httpClientEngine
    )

}