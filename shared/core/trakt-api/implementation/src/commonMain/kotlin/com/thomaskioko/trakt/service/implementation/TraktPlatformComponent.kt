package com.thomaskioko.trakt.service.implementation

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json

typealias TraktHttpClient = HttpClient
typealias TraktHttpClientEngine = HttpClientEngine
typealias TraktJson = Json

expect interface TraktPlatformComponent
