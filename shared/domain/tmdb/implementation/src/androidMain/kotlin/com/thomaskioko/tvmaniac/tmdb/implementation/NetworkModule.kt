package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.network.KtorClientFactory
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    @Named("tmdb-url")
    fun provideTmdbUrl(): String = "https://api.themoviedb.org/"

    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    @Singleton
    @Provides
    fun providehttpClientEngine(): HttpClientEngine = OkHttp.create()

    @Singleton
    @Provides
    @Named("tmdb-http-client")
    fun provideHttpClient(
        @Named("app-build") isDebug: Boolean,
        @Named("tmdb-api-key") tmdbApiKey: String,
        json: Json,
        httpClientEngine: HttpClientEngine
    ): HttpClient =
        KtorClientFactory().httpClient(tmdbHttpClient(isDebug, tmdbApiKey, json, httpClientEngine))

    @Singleton
    @Provides
    fun provideTvShowService(
        @Named("tmdb-http-client") httpClient: HttpClient
    ): TmdbService = TmdbServiceImpl(httpClient)

}
