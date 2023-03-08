package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.trakt.service.implementation.BuildKonfig.TRAKT_CLIENT_ID
import com.thomaskioko.trakt.service.implementation.BuildKonfig.TRAKT_CLIENT_SECRET
import com.thomaskioko.trakt.service.implementation.BuildKonfig.TRAKT_REDIRECT_URI
import com.thomaskioko.tvmaniac.trakt.service.api.TraktService
import io.ktor.http.HttpHeaders
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.setValue

actual fun traktServiceModule() : Module = module {
    single(named("traktClientId")) { TRAKT_CLIENT_ID }
    single(named("traktClientSecret")) { TRAKT_CLIENT_SECRET }
    single(named("traktUri")) { TRAKT_REDIRECT_URI }

    single(named("trakt-json")) { createJson() }
    single(named("trakt-engine")) {
        Darwin.create {
            configureRequest {
                setValue("application/json", HttpHeaders.Accept)
                setValue("2", "trakt-api-version")
                setValue(TRAKT_CLIENT_ID.replace("\"", ""), "trakt-api-key")
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
}

@OptIn(ExperimentalSerializationApi::class)
fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
    explicitNulls = false
}