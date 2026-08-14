package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryEpisode
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistorySeason
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchHistoryShow
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRewatchItemsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
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
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.test.Test

internal class DefaultSimklRewatchRemoteDataSourceTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private fun createDataSource(engine: MockEngine): DefaultSimklRewatchRemoteDataSource {
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
        }
        client.attributes.put(IsAuthenticated) { true }
        return DefaultSimklRewatchRemoteDataSource(httpClient = client)
    }

    @Test
    fun `should use GET method with the rewatch flag and the extended episode params given rewatch sessions are read`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null
        var capturedAllowRewatch: String? = null
        var capturedExtended: String? = null
        var capturedEpisodeWatchedAt: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            capturedAllowRewatch = request.url.parameters["allow_rewatch"]
            capturedExtended = request.url.parameters["extended"]
            capturedEpisodeWatchedAt = request.url.parameters["episode_watched_at"]
            respond(
                content = SIMKL_REWATCH_ITEMS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getRewatchSessions()

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/sync/all-items/shows"
        capturedAllowRewatch shouldBe "yes"
        capturedExtended shouldBe "full"
        capturedEpisodeWatchedAt shouldBe "yes"
    }

    @Test
    fun `should include date_from given a value is provided`() = runTest {
        var capturedDateFrom: String? = null

        val engine = MockEngine { request ->
            capturedDateFrom = request.url.parameters["date_from"]
            respond(
                content = SIMKL_REWATCH_ITEMS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getRewatchSessions(dateFrom = "2026-01-01T00:00:00Z")

        capturedDateFrom shouldBe "2026-01-01T00:00:00Z"
    }

    @Test
    fun `should deserialize the rewatch fields given rewatch sessions are read`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_REWATCH_ITEMS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getRewatchSessions()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklRewatchItemsResponse>>()
        val show = success.body.shows.first()
        show.isRewatch shouldBe true
        show.rewatchId shouldBe 7482L
        show.rewatchStatus shouldBe "active"
        show.watchedEpisodesCount shouldBe 5
        show.show.ids.simkl shouldBe 39687L
    }

    @Test
    fun `should use POST method with the rewatch flag and correct path given rewatch history is added`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null
        var capturedAllowRewatch: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            capturedAllowRewatch = request.url.parameters["allow_rewatch"]
            respond(
                content = SIMKL_ADD_HISTORY_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addRewatchHistory(rewatchHistoryRequest())

        capturedMethod shouldBe HttpMethod.Post
        capturedPath shouldBe "/sync/history"
        capturedAllowRewatch shouldBe "yes"
    }

    @Test
    fun `should put is_rewatch rewatch_id and rewatch_status on the show given rewatch history is added`() = runTest {
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = SIMKL_ADD_HISTORY_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addRewatchHistory(rewatchHistoryRequest())

        val show = json.parseToJsonElement(checkNotNull(capturedBody)).jsonObject["shows"]!!.jsonArray[0].jsonObject
        show["is_rewatch"]!!.jsonPrimitive.boolean.shouldBeTrue()
        show["rewatch_id"]!!.jsonPrimitive.long shouldBe 7482L
        show["rewatch_status"]!!.jsonPrimitive.content shouldBe "active"
    }

    @Test
    fun `should not put is_rewatch rewatch_id or rewatch_status on the episode given rewatch history is added`() = runTest {
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = SIMKL_ADD_HISTORY_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addRewatchHistory(rewatchHistoryRequest())

        val show = json.parseToJsonElement(checkNotNull(capturedBody)).jsonObject["shows"]!!.jsonArray[0].jsonObject
        val episode = show["seasons"]!!.jsonArray[0].jsonObject["episodes"]!!.jsonArray[0].jsonObject
        episode.containsKey("is_rewatch") shouldBe false
        episode.containsKey("rewatch_id") shouldBe false
        episode.containsKey("rewatch_status") shouldBe false
        episode["number"]!!.jsonPrimitive.long shouldBe 1L
    }

    @Test
    fun `should deserialize the rewatch id and status echoed back given rewatch history is added`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_ADD_HISTORY_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.addRewatchHistory(rewatchHistoryRequest())

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklRewatchHistoryResponse>>()
        val status = success.body.added?.statuses?.first()?.response
        status?.rewatchId shouldBe 7482L
        status?.rewatchStatus shouldBe "closed"
    }

    private fun rewatchHistoryRequest(): SimklRewatchHistoryRequest = SimklRewatchHistoryRequest(
        shows = listOf(
            SimklRewatchHistoryShow(
                ids = SimklShowIds(simkl = 17465L),
                rewatchId = 7482L,
                seasons = listOf(
                    SimklRewatchHistorySeason(
                        number = 1,
                        episodes = listOf(
                            SimklRewatchHistoryEpisode(
                                number = 1,
                                watchedAt = "2026-01-01T00:00:00Z",
                            ),
                        ),
                    ),
                ),
            ),
        ),
    )
}

private val SIMKL_REWATCH_ITEMS_RESPONSE = """
{
  "shows": [
    {
      "is_rewatch": true,
      "rewatch_id": 7482,
      "rewatch_status": "active",
      "last_watched_at": "2026-01-01T00:00:00Z",
      "watched_episodes_count": 5,
      "show": {
        "title": "Breaking Bad",
        "ids": {
          "simkl": 39687,
          "tmdb": "1396"
        }
      }
    }
  ]
}
""".trimIndent()

private val SIMKL_ADD_HISTORY_RESPONSE = """
{
  "added": {
    "movies": 0,
    "shows": 0,
    "episodes": 2,
    "statuses": [
      {
        "request": {},
        "response": {
          "status": 3,
          "simkl_type": "tv",
          "anime_type": null,
          "rewatch_id": 7482,
          "rewatch_status": "closed"
        }
      }
    ]
  },
  "not_found": {
    "movies": [],
    "shows": [],
    "episodes": []
  }
}
""".trimIndent()
