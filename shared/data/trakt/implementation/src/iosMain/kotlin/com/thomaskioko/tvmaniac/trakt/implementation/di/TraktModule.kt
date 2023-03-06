package com.thomaskioko.tvmaniac.trakt.implementation.di

import com.thomaskioko.tvmaniac.trakt.api.TraktProfileRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktListCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktStatsCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.trakt.auth.implementation.BuildKonfig
import com.thomaskioko.tvmaniac.trakt.implementation.TraktProfileRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.implementation.TraktShowRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.implementation.TraktServiceImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktFollowedCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktListCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktStatsCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktUserCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktShowCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.traktHttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.setValue

actual fun traktModule(): Module = module {

    single(named("traktClientId")) { BuildKonfig.TRAKT_CLIENT_ID }
    single(named("traktClientSecret")) { BuildKonfig.TRAKT_CLIENT_SECRET }
    single(named("traktUri")) { BuildKonfig.TRAKT_REDIRECT_URI }

    single(named("trakt-json")) { createJson() }
    single(named("trakt-engine")) {
        Darwin.create {
            configureRequest {
                setValue("application/json", HttpHeaders.Accept)
                setValue("2", "trakt-api-version")
                setValue(BuildKonfig.TRAKT_CLIENT_ID.replace("\"", ""), "trakt-api-key")
            }
        }
    }

    single(named("traktHttpClient")) {
        traktHttpClient(
            true,
            get(named("trakt-json")),
            get(named("trakt-engine"))
        )
    }

    single<TraktService> {
        TraktServiceImpl(
            get(named("traktClientId")),
            get(named("traktClientSecret")),
            get(named("traktUri")),
            get(named("traktHttpClient"))
        )
    }
    single<TraktUserCache> {
        TraktUserCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }
    single<TraktStatsCache> {
        TraktStatsCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }
    single<TraktListCache> {
        TraktListCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }
    single<TraktFollowedCache> {
        TraktFollowedCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }

    single<TraktShowRepository> {
        TraktShowRepositoryImpl(
            tvShowCache = get(),
            traktUserCache = get(),
            followedCache = get(),
            categoryCache = get(),
            traktService = get(),
            dateUtilHelper = get(),
            dispatcher = Dispatchers.Default
        )
    }

    single<TraktProfileRepository> {
        TraktProfileRepositoryImpl(
            traktService = get(),
            traktListCache = get(),
            statsCache = get(),
            traktUserCache = get(),
            followedCache = get(),
            dateUtilHelper = get(),
            dispatcher = Dispatchers.Default
        )
    }

    single<TvShowCache> { TraktShowCacheImpl(
        database = get(),
        coroutineContext = Dispatchers.Default
    ) }
}

@OptIn(ExperimentalSerializationApi::class)
fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
    explicitNulls = false
}