package com.thomaskioko.tvmaniac.tmdb.implementation.inject

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json


typealias TmdbHttpClient = HttpClient
typealias TmdbHttpClientEngine = HttpClientEngine
typealias TmdbJson = Json

expect interface TmdbPlatformComponent