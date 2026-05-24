package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.trakt.service.implementation.TraktAuthGuard
import com.thomaskioko.trakt.service.implementation.loadJson
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test

class DefaultTraktListRemoteDataSourceTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private fun createDataSource(engine: MockEngine): DefaultTraktListRemoteDataSource {
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
        }
        client.attributes.put(IsAuthenticated) { true }
        return DefaultTraktListRemoteDataSource(httpClient = client)
    }

    @Test
    fun `should use GET method and correct path given getUser is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null
        var capturedExtended: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            capturedExtended = request.url.parameters["extended"]
            respond(
                content = loadJson("trakt_user_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getUser("me")

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/users/me"
        capturedExtended shouldBe "full"
    }

    @Test
    fun `should return Success given getUser succeeds`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = loadJson("trakt_user_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getUser("me")

        val success = result.shouldBeInstanceOf<ApiResponse.Success<TraktUserResponse>>()
        success.body.userName shouldBe "sean"
        success.body.ids.slug shouldBe "sean"
    }

    @Test
    fun `should use GET and correct path given getUserList is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = """[]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getUserList("sean")

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/users/sean/lists"
    }

    @Test
    fun `should use GET and correct path given getListItems is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = """[]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getListItems(userSlug = "sean", listId = 42L)

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/users/sean/lists/42/items"
    }

    @Test
    fun `should batch all trakt ids into a single POST given addShowsToWatchListByTraktIds`() = runTest {
        var requestCount = 0
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            requestCount++
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = loadJson("trakt_add_show_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addShowsToWatchListByTraktIds(traktIds = listOf(101L, 202L, 303L))

        requestCount shouldBe 1
        capturedBody shouldContain "101"
        capturedBody shouldContain "202"
        capturedBody shouldContain "303"
    }

    @Test
    fun `should batch all trakt ids into a single POST given removeShowsFromWatchListByTraktIds`() = runTest {
        var requestCount = 0
        var capturedPath: String? = null
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            requestCount++
            capturedPath = request.url.encodedPath
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = """{"deleted":{"shows":2},"not_found":{"shows":[]},"list":{"item_count":0,"updated_at":"2026-01-01T00:00:00Z"}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.removeShowsFromWatchListByTraktIds(traktIds = listOf(404L, 505L))

        requestCount shouldBe 1
        capturedPath shouldBe "/sync/watchlist/remove"
        capturedBody shouldContain "404"
        capturedBody shouldContain "505"
    }

    @Test
    fun `should use POST with body given addShowToWatchListByTmdbId is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = loadJson("trakt_add_show_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addShowToWatchListByTmdbId(tmdbId = 12345)

        capturedMethod shouldBe HttpMethod.Post
        capturedPath shouldBe "/sync/watchlist"
        capturedBody shouldContain "12345"
    }

    @Test
    fun `should return HttpError given server returns unauthorized`() = runTest {
        val engine = MockEngine { _ ->
            respondError(
                status = HttpStatusCode.Unauthorized,
                content = loadJson("trakt_error_response.json"),
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
            expectSuccess = true
        }
        client.attributes.put(IsAuthenticated) { true }
        val dataSource = DefaultTraktListRemoteDataSource(httpClient = client)

        val result = dataSource.getUser("me")

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<TraktUserResponse>>()
        result.code shouldBe 401
    }

    @Test
    fun `should return Success given auth guard allows authenticated request`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """[]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
            install(TraktAuthGuard) {
                isAuthenticated = { true }
            }
        }
        client.attributes.put(IsAuthenticated) { true }
        val dataSource = DefaultTraktListRemoteDataSource(httpClient = client)

        val result = dataSource.getUserList("sean")

        result.shouldBeInstanceOf<ApiResponse.Success<*>>()
    }

    @Test
    fun `should return Unauthenticated given user is not authenticated`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """{}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
            install(TraktAuthGuard) {
                isAuthenticated = { false }
            }
        }
        client.attributes.put(IsAuthenticated) { false }
        val dataSource = DefaultTraktListRemoteDataSource(httpClient = client)

        val result = dataSource.getUserList("sean")

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }
}
