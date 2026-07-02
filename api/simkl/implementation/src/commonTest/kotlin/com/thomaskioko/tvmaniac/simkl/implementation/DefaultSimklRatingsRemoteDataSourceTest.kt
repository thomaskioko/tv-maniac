package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingIdItem
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingItem
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowSummaryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserRatingsResponse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
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

internal class DefaultSimklRatingsRemoteDataSourceTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private fun createDataSource(engine: MockEngine): DefaultSimklRatingsRemoteDataSource {
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
        }
        client.attributes.put(IsAuthenticated) { true }
        return DefaultSimklRatingsRemoteDataSource(httpClient = client)
    }

    @Test
    fun `should use POST method and correct path given addRatings is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = SIMKL_ADD_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addRatings(
            SimklRatingsRequest(
                shows = listOf(
                    SimklRatingItem(
                        rating = 8,
                        ratedAt = "2014-09-01T09:10:11.000Z",
                        ids = SimklShowIds(simkl = 39687L, tmdb = "296", imdb = "tt0472954"),
                    ),
                ),
            ),
        )

        capturedMethod shouldBe HttpMethod.Post
        capturedPath shouldBe "/sync/ratings"
    }

    @Test
    fun `should include rating and rated_at in body given addRatings is called`() = runTest {
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = SIMKL_ADD_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addRatings(
            SimklRatingsRequest(
                shows = listOf(
                    SimklRatingItem(
                        rating = 8,
                        ratedAt = "2014-09-01T09:10:11.000Z",
                        ids = SimklShowIds(simkl = 39687L, tmdb = "296", imdb = "tt0472954"),
                    ),
                ),
            ),
        )

        capturedBody shouldContain "\"rating\": 8"
        capturedBody shouldContain "\"rated_at\": \"2014-09-01T09:10:11.000Z\""
        capturedBody shouldContain "\"simkl\": 39687"
    }

    @Test
    fun `should deserialize added and not_found buckets given addRatings response is returned`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_ADD_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.addRatings(SimklRatingsRequest(shows = emptyList()))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklAddRatingsResponse>>()
        success.body.added?.movies shouldBe 0
        success.body.added?.shows shouldBe 1
        success.body.notFound?.movies shouldBe emptyList()
        success.body.notFound?.shows shouldBe emptyList()
    }

    @Test
    fun `should use POST method and correct path given removeRatings is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = SIMKL_REMOVE_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.removeRatings(
            SimklRemoveRatingsRequest(
                shows = listOf(SimklRatingIdItem(ids = SimklShowIds(simkl = 17465L))),
            ),
        )

        capturedMethod shouldBe HttpMethod.Post
        capturedPath shouldBe "/sync/ratings/remove"
    }

    @Test
    fun `should include only ids without rating in body given removeRatings is called`() = runTest {
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = SIMKL_REMOVE_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.removeRatings(
            SimklRemoveRatingsRequest(
                shows = listOf(SimklRatingIdItem(ids = SimklShowIds(simkl = 17465L))),
            ),
        )

        capturedBody shouldContain "\"simkl\": 17465"
        (capturedBody?.contains("rating") ?: true) shouldBe false
    }

    @Test
    fun `should deserialize deleted and not_found buckets given removeRatings response is returned`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_REMOVE_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.removeRatings(SimklRemoveRatingsRequest(shows = emptyList()))

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklRemoveRatingsResponse>>()
        success.body.deleted?.movies shouldBe 0
        success.body.deleted?.shows shouldBe 1
        success.body.notFound?.movies shouldBe emptyList()
        success.body.notFound?.shows shouldBe emptyList()
    }

    @Test
    fun `should use GET method and correct path given getUserShowRatings is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = SIMKL_USER_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getUserShowRatings()

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/sync/ratings/shows"
    }

    @Test
    fun `should deserialize user_rating rated_at and show given getUserShowRatings returns ratings`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_USER_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getUserShowRatings()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklUserRatingsResponse>>()
        val ratedShow = success.body.shows.first()
        ratedShow.userRating shouldBe 8
        ratedShow.ratedAt shouldBe "2014-09-01T09:10:11Z"
        ratedShow.show.title shouldBe "Breaking Bad"
        ratedShow.show.ids.simkl shouldBe 39687L
    }

    @Test
    fun `should deserialize simkl community rating and votes given show entry includes a ratings object`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_USER_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getUserShowRatings()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklUserRatingsResponse>>()
        val ratedShow = success.body.shows.first()
        ratedShow.show.ratings?.simkl?.rating shouldBe 8.4
        ratedShow.show.ratings?.simkl?.votes shouldBe 12345
    }

    @Test
    fun `should use GET method with extended full param and correct path given getShowSummary is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null
        var capturedExtendedParam: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            capturedExtendedParam = request.url.parameters["extended"]
            respond(
                content = SIMKL_SHOW_SUMMARY_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getShowSummary(simklId = 39687L)

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/tv/39687"
        capturedExtendedParam shouldBe "full"
    }

    @Test
    fun `should deserialize simkl community rating and votes given getShowSummary returns ratings`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_SHOW_SUMMARY_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getShowSummary(simklId = 39687L)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklShowSummaryResponse>>()
        success.body.ratings?.simkl?.rating shouldBe 8.4
        success.body.ratings?.simkl?.votes shouldBe 123
    }

    @Test
    fun `should return Unauthenticated given user is not authenticated and getUserShowRatings is called`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_USER_RATINGS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
            install(SimklAuthGuard) {
                isAuthenticated = { false }
            }
        }
        client.attributes.put(IsAuthenticated) { false }
        val dataSource = DefaultSimklRatingsRemoteDataSource(httpClient = client)

        val result = dataSource.getUserShowRatings()

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }
}

private val SIMKL_ADD_RATINGS_RESPONSE = """
{
  "added": {
    "movies": 0,
    "shows": 1
  },
  "not_found": {
    "movies": [],
    "shows": []
  }
}
""".trimIndent()

private val SIMKL_REMOVE_RATINGS_RESPONSE = """
{
  "deleted": {
    "movies": 0,
    "shows": 1
  },
  "not_found": {
    "movies": [],
    "shows": []
  }
}
""".trimIndent()

private val SIMKL_SHOW_SUMMARY_RESPONSE = """
{
  "ratings": {
    "simkl": {
      "rating": 8.4,
      "votes": 123
    }
  }
}
""".trimIndent()

private val SIMKL_USER_RATINGS_RESPONSE = """
{
  "shows": [
    {
      "user_rating": 8,
      "rated_at": "2014-09-01T09:10:11Z",
      "show": {
        "title": "Breaking Bad",
        "year": 2008,
        "ids": {
          "simkl": 39687,
          "tmdb": "1396",
          "imdb": "tt0903747"
        },
        "ratings": {
          "simkl": {
            "rating": 8.4,
            "votes": 12345
          }
        }
      }
    }
  ]
}
""".trimIndent()
