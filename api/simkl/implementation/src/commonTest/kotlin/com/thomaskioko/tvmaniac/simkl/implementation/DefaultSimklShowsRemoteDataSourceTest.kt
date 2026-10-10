package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklSearchShowResponse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test

internal class DefaultSimklShowsRemoteDataSourceTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private fun createDataSource(engine: MockEngine): DefaultSimklShowsRemoteDataSource {
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
        }
        return DefaultSimklShowsRemoteDataSource(httpClient = client)
    }

    @Test
    fun `should use GET method with q extended and limit params given searchShows is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null
        var capturedAuthHeader: String? = null
        var capturedQParam: String? = null
        var capturedExtendedParam: String? = null
        var capturedLimitParam: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            capturedAuthHeader = request.headers[HttpHeaders.Authorization]
            capturedQParam = request.url.parameters["q"]
            capturedExtendedParam = request.url.parameters["extended"]
            capturedLimitParam = request.url.parameters["limit"]
            respond(
                content = SIMKL_SEARCH_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.searchShows(query = "breaking bad", limit = 30)

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/search/tv"
        capturedAuthHeader.shouldBeNull()
        capturedQParam shouldBe "breaking bad"
        capturedExtendedParam shouldBe "full"
        capturedLimitParam shouldBe "30"
    }

    @Test
    fun `should map tmdb and simkl_id ids given searchShows returns results`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_SEARCH_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.searchShows(query = "breaking bad", limit = 30)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<SimklSearchShowResponse>>>()
        val show = success.body.first()
        show.title shouldBe "Breaking Bad"
        show.year shouldBe 2008
        show.ids.simklId shouldBe 39687L
        show.ids.tmdb shouldBe "1396"
    }
}

private val SIMKL_SEARCH_RESPONSE = """
[
  {
    "title": "Breaking Bad",
    "year": 2008,
    "poster": "abcdef",
    "ids": {
      "simkl_id": 39687,
      "slug": "breaking-bad",
      "tmdb": "1396",
      "imdb": "tt0903747",
      "tvdb": "81189"
    }
  }
]
""".trimIndent()
