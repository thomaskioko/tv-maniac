package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import io.ktor.client.engine.darwin.Darwin
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun tmdbModule(): Module = module {

    single { createJson() }
    single { Darwin.create() }
    single(named("TMDB_API_KEY")) { BuildKonfig.TMDB_API_KEY }
    single {
        tmdbHttpClient(
            true, //TODO:: provide buildType
            get(named("TMDB_API_KEY")),
            get(),
            get()
        )
    }
    single<TmdbService> { TmdbServiceImpl(get()) }
    single<ShowImageCache> { ShowImageCacheImpl(get()) }
}


fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
    explicitNulls = false
}