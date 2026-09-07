package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.trakt.service.implementation.TraktAuthGuard
import com.thomaskioko.trakt.service.implementation.loadJson
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
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
        var capturedPage: String? = null
        var capturedLimit: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            capturedPage = request.url.parameters["page"]
            capturedLimit = request.url.parameters["limit"]
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
        capturedPage shouldBe "1"
        capturedLimit shouldBe "250"
    }

    @Test
    fun `should send the given page and limit given getListItems is called with them`() = runTest {
        var capturedPage: String? = null
        var capturedLimit: String? = null

        val engine = MockEngine { request ->
            capturedPage = request.url.parameters["page"]
            capturedLimit = request.url.parameters["limit"]
            respond(
                content = """[]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getListItems(userSlug = "sean", listId = 42L, page = 3, limit = 50)

        capturedPage shouldBe "3"
        capturedLimit shouldBe "50"
    }

    @Test
    fun `should send page, limit and sort headers given getWatchList is called`() = runTest {
        var capturedPath: String? = null
        var capturedPage: String? = null
        var capturedLimit: String? = null
        var capturedSortBy: String? = null
        var capturedSortHow: String? = null

        val engine = MockEngine { request ->
            capturedPath = request.url.encodedPath
            capturedPage = request.url.parameters["page"]
            capturedLimit = request.url.parameters["limit"]
            capturedSortBy = request.headers["X-Sort-By"]
            capturedSortHow = request.headers["X-Sort-How"]
            respond(
                content = """[]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getWatchList(sortBy = "added", sortHow = "desc")

        capturedPath shouldBe "/users/me/watchlist/shows"
        capturedPage shouldBe "1"
        capturedLimit shouldBe "250"
        capturedSortBy shouldBe "added"
        capturedSortHow shouldBe "desc"
    }

    @Test
    fun `should batch all shows into a single POST given addShowsToWatchList`() = runTest {
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

        dataSource.addShowsToWatchList(
            shows = listOf(
                TraktShowIds(traktId = 101L),
                TraktShowIds(traktId = 202L),
                TraktShowIds(traktId = 303L),
            ),
        )

        requestCount shouldBe 1
        capturedBody shouldContain "101"
        capturedBody shouldContain "202"
        capturedBody shouldContain "303"
    }

    @Test
    fun `should send the tmdb id under the tmdb key given the show has no trakt id`() = runTest {
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = loadJson("trakt_add_show_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addShowsToWatchList(shows = listOf(TraktShowIds(tmdbId = 1396L)))

        capturedBody shouldContain "\"tmdb\": 1396"
        capturedBody!! shouldNotContain "trakt"
    }

    @Test
    fun `should batch all shows into a single POST given removeShowsFromWatchList`() = runTest {
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

        dataSource.removeShowsFromWatchList(
            shows = listOf(TraktShowIds(traktId = 404L), TraktShowIds(traktId = 505L)),
        )

        requestCount shouldBe 1
        capturedPath shouldBe "/sync/watchlist/remove"
        capturedBody shouldContain "404"
        capturedBody shouldContain "505"
    }

    @Test
    fun `should use PUT method, correct path and body given updateList is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = "",
                status = HttpStatusCode.NoContent,
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.updateList(userSlug = "sean", listId = 42L, name = "Renamed")

        capturedMethod shouldBe HttpMethod.Put
        capturedPath shouldBe "/users/sean/lists/42"
        capturedBody shouldContain "\"name\": \"Renamed\""
    }

    @Test
    fun `should use DELETE method and correct path given deleteList is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = "",
                status = HttpStatusCode.NoContent,
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.deleteList(userSlug = "sean", listId = 42L)

        capturedMethod shouldBe HttpMethod.Delete
        capturedPath shouldBe "/users/sean/lists/42"
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
