package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test

class DefaultTraktShowsRemoteDataSourceTest {

    private val capturedQueries = mutableListOf<Map<String, String?>>()

    private val dataSource = DefaultTraktShowsRemoteDataSource(
        httpClient = HttpClient(
            MockEngine { request ->
                capturedQueries += request.url.parameters.names().associateWith { request.url.parameters[it] }
                respond(
                    content = "[]",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            },
        ) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }.also { it.attributes.put(IsAuthenticated) { true } },
    )

    @Test
    fun `should send page and limit given each paged show list is requested`() = runTest {
        dataSource.getTrendingShows(page = 3, limit = 20)
        dataSource.getPopularShows(page = 3, limit = 20)
        dataSource.getFavoritedShows(page = 3, limit = 20)
        dataSource.getMostWatchedShows(page = 3, limit = 20)

        capturedQueries.size shouldBe 4
        capturedQueries.forEach { query ->
            query["page"] shouldBe "3"
            query["limit"] shouldBe "20"
            query["extended"] shouldBe "full"
        }
    }
}
