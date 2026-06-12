package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklUserStatsResponse
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

internal class DefaultSimklUserRemoteDataSourceTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private fun createDataSource(engine: MockEngine): DefaultSimklUserRemoteDataSource {
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
        }
        client.attributes.put(IsAuthenticated) { true }
        return DefaultSimklUserRemoteDataSource(httpClient = client)
    }

    @Test
    fun `should use POST method and correct path given getUserStats is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = SIMKL_STATS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getUserStats(userId = 12345678L)

        capturedMethod shouldBe HttpMethod.Post
        capturedPath shouldBe "/users/12345678/stats"
    }

    @Test
    fun `should map tv and anime bucket counts given getUserStats returns status buckets`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_STATS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getUserStats(userId = 12345678L)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<SimklUserStatsResponse>>()
        val stats = success.body
        stats.totalMins shouldBe 97200
        stats.tv?.watching?.count shouldBe 5
        stats.tv?.watching?.watchedEpisodesCount shouldBe 120
        stats.tv?.completed?.count shouldBe 30
        stats.tv?.completed?.watchedEpisodesCount shouldBe 800
        stats.anime?.watching?.count shouldBe 3
        stats.anime?.completed?.count shouldBe 12
    }

    @Test
    fun `should return Unauthenticated given user is not authenticated`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = SIMKL_STATS_RESPONSE,
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
        val dataSource = DefaultSimklUserRemoteDataSource(httpClient = client)

        val result = dataSource.getUserStats(userId = 12345678L)

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }
}

private val SIMKL_STATS_RESPONSE = """
{
  "total_mins": 97200,
  "tv": {
    "total_mins": 60000,
    "watching": {
      "count": 5,
      "watched_episodes_count": 120,
      "total_episodes_count": 200,
      "left_to_watch_episodes": 80,
      "left_to_watch_mins": 3200
    },
    "completed": {
      "count": 30,
      "watched_episodes_count": 800,
      "total_episodes_count": 800,
      "left_to_watch_episodes": 0,
      "left_to_watch_mins": 0
    },
    "hold": {
      "count": 2,
      "watched_episodes_count": 40,
      "total_episodes_count": 100,
      "left_to_watch_episodes": 60,
      "left_to_watch_mins": 2400
    },
    "plantowatch": {
      "count": 15,
      "watched_episodes_count": 0,
      "total_episodes_count": 500,
      "left_to_watch_episodes": 500,
      "left_to_watch_mins": 20000
    }
  },
  "anime": {
    "total_mins": 25200,
    "watching": {
      "count": 3,
      "watched_episodes_count": 48,
      "total_episodes_count": 96,
      "left_to_watch_episodes": 48,
      "left_to_watch_mins": 1200
    },
    "completed": {
      "count": 12,
      "watched_episodes_count": 288,
      "total_episodes_count": 288,
      "left_to_watch_episodes": 0,
      "left_to_watch_mins": 0
    },
    "hold": {
      "count": 1,
      "watched_episodes_count": 10,
      "total_episodes_count": 24,
      "left_to_watch_episodes": 14,
      "left_to_watch_mins": 350
    },
    "plantowatch": {
      "count": 8,
      "watched_episodes_count": 0,
      "total_episodes_count": 200,
      "left_to_watch_episodes": 200,
      "left_to_watch_mins": 5000
    }
  },
  "movies": {
    "total_mins": 12000,
    "completed": {
      "count": 50,
      "mins": 10000
    },
    "plantowatch": {
      "count": 20,
      "mins": 4000
    },
    "dropped": {
      "count": 3,
      "mins": 300
    }
  }
}
""".trimIndent()
