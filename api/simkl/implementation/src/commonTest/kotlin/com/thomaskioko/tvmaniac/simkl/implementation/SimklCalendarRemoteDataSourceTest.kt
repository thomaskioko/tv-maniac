package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.calendar.RemoteCalendarEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsDao
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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
import kotlin.time.Instant

internal class SimklCalendarRemoteDataSourceTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private fun buildSource(
        engine: MockEngine,
        followedEntries: List<FollowedShowEntry> = emptyList(),
    ): SimklCalendarRemoteDataSource {
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
        }
        val dao = FakeFollowedShowsDao()
        dao.setEntries(followedEntries)
        return SimklCalendarRemoteDataSource(
            httpClient = client,
            followedShowsDao = dao,
        )
    }

    @Test
    fun `should report simkl as its provider`() {
        val source = buildSource(
            engine = MockEngine { respond("[]", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json")) },
        )
        source.provider shouldBe AccountProvider.SIMKL
    }

    @Test
    fun `should return empty list given no shows are tracked`() = runTest {
        val engine = MockEngine { respond("[]", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json")) }

        val source = buildSource(engine = engine, followedEntries = emptyList())

        val result = source.getCalendarEntries(startDate = "2026-04-19", days = 7)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteCalendarEntry>>>()
        success.body.shouldBeEmpty()
    }

    @Test
    fun `should filter entries to only tracked shows given mixed tmdb ids in feed`() = runTest {
        val trackedTmdbId = 1396L
        val untrackedTmdbId = 1399L

        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            val body = when {
                path.endsWith("tv.json") -> TV_FEED_WITH_TWO_SHOWS
                else -> "[]"
            }
            respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }

        val source = buildSource(
            engine = engine,
            followedEntries = listOf(
                followedEntry(tmdbId = trackedTmdbId),
            ),
        )

        val result = source.getCalendarEntries(startDate = "2026-04-19", days = 7)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteCalendarEntry>>>()
        success.body.all { it.tmdbId == trackedTmdbId } shouldBe true
        success.body.none { it.tmdbId == untrackedTmdbId } shouldBe true
    }

    @Test
    fun `should merge tv and anime entries given both feeds have tracked shows`() = runTest {
        val tvTmdbId = 1396L
        val animeTmdbId = 60625L

        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            val body = when {
                path.endsWith("tv.json") -> TV_FEED_SINGLE_SHOW
                path.endsWith("anime.json") -> ANIME_FEED_SINGLE_SHOW
                else -> "[]"
            }
            respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }

        val source = buildSource(
            engine = engine,
            followedEntries = listOf(
                followedEntry(tmdbId = tvTmdbId),
                followedEntry(tmdbId = animeTmdbId),
            ),
        )

        val result = source.getCalendarEntries(startDate = "2026-04-19", days = 7)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteCalendarEntry>>>()
        success.body.size shouldBe 2
        success.body.map { it.tmdbId }.toSet() shouldBe setOf(tvTmdbId, animeTmdbId)
    }

    @Test
    fun `should map calendar entry fields correctly given valid feed entry`() = runTest {
        val trackedTmdbId = 1396L

        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            val body = when {
                path.endsWith("tv.json") -> TV_FEED_SINGLE_SHOW
                else -> "[]"
            }
            respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }

        val source = buildSource(
            engine = engine,
            followedEntries = listOf(followedEntry(tmdbId = trackedTmdbId)),
        )

        val result = source.getCalendarEntries(startDate = "2026-04-19", days = 7)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteCalendarEntry>>>()
        val entry = success.body.single()
        entry.tmdbId shouldBe trackedTmdbId
        entry.showTitle shouldBe "Breaking Bad"
        entry.episodeTitle.shouldBeNull()
        entry.seasonNumber shouldBe 1
        entry.episodeNumber shouldBe 1
        entry.firstAiredIso shouldBe "2026-04-19T03:00:00.000Z"
        entry.runtime.shouldBeNull()
    }

    @Test
    fun `should default season to 1 given anime entry without a season`() = runTest {
        val animeTmdbId = 60625L

        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            val body = when {
                path.endsWith("anime.json") -> ANIME_FEED_SINGLE_SHOW
                else -> "[]"
            }
            respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }

        val source = buildSource(
            engine = engine,
            followedEntries = listOf(followedEntry(tmdbId = animeTmdbId)),
        )

        val result = source.getCalendarEntries(startDate = "2026-04-19", days = 7)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteCalendarEntry>>>()
        success.body.single().seasonNumber shouldBe 1
    }

    @Test
    fun `should skip entries without tmdb id given feed contains entries missing ids`() = runTest {
        val trackedTmdbId = 1396L

        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            val body = when {
                path.endsWith("tv.json") -> TV_FEED_WITH_MISSING_IDS
                else -> "[]"
            }
            respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }

        val source = buildSource(
            engine = engine,
            followedEntries = listOf(followedEntry(tmdbId = trackedTmdbId)),
        )

        val result = source.getCalendarEntries(startDate = "2026-04-19", days = 7)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteCalendarEntry>>>()
        success.body.shouldBeEmpty()
    }
}

private fun followedEntry(tmdbId: Long): FollowedShowEntry = FollowedShowEntry(
    showId = tmdbId,
    tmdbId = tmdbId,
    followedAt = Instant.fromEpochSeconds(0),
)

private val TV_FEED_SINGLE_SHOW = """
[
  {
    "date": "2026-04-19T03:00:00.000Z",
    "title": "Breaking Bad",
    "ids": {
      "simkl_id": 583436,
      "tmdb": "1396",
      "imdb": "tt0903747"
    },
    "episode": {
      "season": 1,
      "episode": 1
    }
  }
]
""".trimIndent()

private val TV_FEED_WITH_TWO_SHOWS = """
[
  {
    "date": "2026-04-19T03:00:00.000Z",
    "title": "Breaking Bad",
    "ids": {
      "simkl_id": 583436,
      "tmdb": "1396",
      "imdb": "tt0903747"
    },
    "episode": {
      "season": 1,
      "episode": 1
    }
  },
  {
    "date": "2026-04-26T03:00:00.000Z",
    "title": "Game of Thrones",
    "ids": {
      "simkl_id": 1399,
      "tmdb": "1399",
      "imdb": "tt0944947"
    },
    "episode": {
      "season": 1,
      "episode": 1
    }
  }
]
""".trimIndent()

private val ANIME_FEED_SINGLE_SHOW = """
[
  {
    "date": "2026-04-20T14:00:00.000Z",
    "title": "Attack on Titan",
    "ids": {
      "simkl_id": 60625,
      "tmdb": "60625",
      "mal": "16498"
    },
    "episode": {
      "episode": 1
    }
  }
]
""".trimIndent()

private val TV_FEED_WITH_MISSING_IDS = """
[
  {
    "date": "2026-04-19T03:00:00.000Z",
    "title": "No IDs Show",
    "episode": {
      "season": 1,
      "episode": 1
    }
  }
]
""".trimIndent()
