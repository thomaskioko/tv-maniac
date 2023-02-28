package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbRepositoryImpl
import io.ktor.client.engine.darwin.Darwin
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun tmdbModule(): Module = module {

    single { createJson() }
    single { Darwin.create() }
    single {
        tmdbHttpClient(
            isDebug = false, //TODO:: provide buildType
            json = get(),
            httpClientEngine = get(),
            tmdbApiKey = BuildKonfig.TMDB_API_KEY.replace("\"", "")
        )
    }
    single<TmdbService> { TmdbServiceImpl(get()) }
    single<ShowImageCache> { ShowImageCacheImpl(get(), get()) }
    single<TmdbRepository> { TmdbRepositoryImpl(get(), get(), get(), get()) }
}


fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
    explicitNulls = false
}