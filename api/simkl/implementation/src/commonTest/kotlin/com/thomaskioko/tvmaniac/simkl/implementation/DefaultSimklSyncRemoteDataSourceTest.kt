package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddToListRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddToListResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAllItemsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklListShow
import com.thomaskioko.tvmaniac.simkl.api.model.SimklListStatus
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds
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

internal class DefaultSimklSyncRemoteDataSourceTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private fun createDataSource(engine: MockEngine): DefaultSimklSyncRemoteDataSource {
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
        }
        client.attributes.put(IsAuthenticated) { true }
        return DefaultSimklSyncRemoteDataSource(httpClient = client)
    }

    @Test
    fun `should post the show to the plan to watch list given addToList is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = SIMKL_ADD_TO_LIST_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addToList(
            SimklAddToListRequest(
                shows = listOf(
                    SimklListShow(
                        ids = SimklShowIds(tmdb = "1396"),
                        to = SimklListStatus.PLAN_TO_WATCH,
                    ),
                ),
            ),
        )

        capturedMethod shouldBe HttpMethod.Post
        capturedPath shouldBe "/sync/add-to-list"
        capturedBody shouldContain "\"tmdb\": \"1396\""
        capturedBody shouldContain "\"to\": \"plantowatch\""
    }

    @Test
    fun `should report shows Simkl could not match given addToList is called`() = runTest {
        val engine = MockEngine {
            respond(
                content = SIMKL_ADD_TO_LIST_NOT_FOUND_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.addToList(
            SimklAddToListRequest(
                shows = listOf(
                    SimklListShow(
                        ids = SimklShowIds(tmdb = "999999"),
                        to = SimklListStatus.PLAN_TO_WATCH,
                    ),
                ),
            ),
        )

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklAddToListResponse>>()
        success.body.notFound?.shows?.size shouldBe 1
    }

    @Test
    fun `should use GET method and correct path given getAllWatchedShows is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = SIMKL_ALL_ITEMS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getAllWatchedShows()

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/sync/all-items/shows"
    }

    @Test
    fun `should include extended and episode_watched_at query params given getAllWatchedShows is called without dateFrom`() = runTest {
        var capturedQuery: String? = null

        val engine = MockEngine { request ->
            capturedQuery = request.url.encodedQuery
            respond(
                content = SIMKL_ALL_ITEMS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getAllWatchedShows()

        capturedQuery shouldBe "extended=full&episode_watched_at=yes"
    }

    @Test
    fun `should include date_from query param given getAllWatchedShows is called with dateFrom`() = runTest {
        var capturedQuery: String? = null

        val engine = MockEngine { request ->
            capturedQuery = request.url.encodedQuery
            respond(
                content = SIMKL_ALL_ITEMS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getAllWatchedShows(dateFrom = "2024-01-01T00:00:00Z")

        capturedQuery shouldBe "extended=full&episode_watched_at=yes&date_from=2024-01-01T00%3A00%3A00Z"
    }

    @Test
    fun `should deserialize show title year and ids given getAllWatchedShows returns a watched show`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_ALL_ITEMS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getAllWatchedShows()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklAllItemsResponse>>()
        val show = success.body.shows.first()
        show.status shouldBe "completed"
        show.lastWatchedAt shouldBe "2024-09-01T09:10:11Z"
        show.show.title shouldBe "Emerald City"
        show.show.year shouldBe 2017
        show.show.ids.simkl shouldBe 583436L
        show.show.ids.tmdb shouldBe "62417"
        show.show.ids.imdb shouldBe "tt3579018"
        show.show.ids.tvdb shouldBe "295779"
    }

    @Test
    fun `should deserialize seasons and episodes given getAllWatchedShows returns extended data`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_ALL_ITEMS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getAllWatchedShows()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklAllItemsResponse>>()
        val show = success.body.shows.first()
        show.seasons.size shouldBe 1
        val season = show.seasons.first()
        season.number shouldBe 1
        season.episodes.size shouldBe 1
        val episode = season.episodes.first()
        episode.number shouldBe 1
        episode.watchedAt shouldBe "2024-09-01T09:10:11Z"
    }

    @Test
    fun `should tolerate absent tmdb id given getAllWatchedShows returns show with missing tmdb`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_ALL_ITEMS_RESPONSE_MISSING_TMDB,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getAllWatchedShows()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklAllItemsResponse>>()
        val show = success.body.shows.first()
        show.show.ids.simkl shouldBe 583436L
        show.show.ids.tmdb shouldBe null
    }

    @Test
    fun `should return Unauthenticated given user is not authenticated`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_ALL_ITEMS_RESPONSE,
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
        val dataSource = DefaultSimklSyncRemoteDataSource(httpClient = client)

        val result = dataSource.getAllWatchedShows()

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }
}

private val SIMKL_ALL_ITEMS_RESPONSE = """
{
  "shows": [
    {
      "status": "completed",
      "last_watched_at": "2024-09-01T09:10:11Z",
      "show": {
        "title": "Emerald City",
        "year": 2017,
        "ids": {
          "simkl": 583436,
          "tmdb": "62417",
          "imdb": "tt3579018",
          "tvdb": "295779"
        }
      },
      "seasons": [
        {
          "number": 1,
          "episodes": [
            {
              "number": 1,
              "watched_at": "2024-09-01T09:10:11Z"
            }
          ]
        }
      ]
    }
  ]
}
""".trimIndent()

private val SIMKL_ALL_ITEMS_RESPONSE_MISSING_TMDB = """
{
  "shows": [
    {
      "status": "watching",
      "show": {
        "title": "No TMDB Show",
        "year": 2020,
        "ids": {
          "simkl": 583436,
          "imdb": "tt3579018"
        }
      },
      "seasons": []
    }
  ]
}
""".trimIndent()

private val SIMKL_ADD_TO_LIST_RESPONSE = """
{
  "added": {
    "shows": [
      {
        "title": "Breaking Bad",
        "year": 2008,
        "to": "plantowatch",
        "ids": {
          "simkl": 2090,
          "tmdb": "1396"
        }
      }
    ]
  },
  "not_found": {
    "shows": []
  }
}
""".trimIndent()

private val SIMKL_ADD_TO_LIST_NOT_FOUND_RESPONSE = """
{
  "added": {
    "shows": []
  },
  "not_found": {
    "shows": [
      {
        "ids": {
          "tmdb": "999999"
        }
      }
    ]
  }
}
""".trimIndent()
