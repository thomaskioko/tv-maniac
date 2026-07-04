package com.thomaskioko.trakt.service.implementation.api

import com.thomaskioko.trakt.service.implementation.TraktAuthGuard
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeRatingIdItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeRatingItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonRatingIdItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonRatingItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowRatingIdItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowRatingItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserRatingItem
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
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

class DefaultTraktRatingsRemoteDataSourceTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private fun createDataSource(engine: MockEngine): DefaultTraktRatingsRemoteDataSource {
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(json = json) }
        }
        client.attributes.put(IsAuthenticated) { true }
        return DefaultTraktRatingsRemoteDataSource(httpClient = client)
    }

    @Test
    fun `should use POST method and correct path given addRatings is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = """{"added":{"movies":0,"shows":1,"seasons":0,"episodes":0},""" +
                    """"not_found":{"movies":[],"shows":[],"seasons":[],"episodes":[]}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addRatings(
            TraktRatingsRequest(shows = listOf(TraktShowRatingItem(rating = 8, ids = TraktShowIds(traktId = 1, tmdbId = 296)))),
        )

        capturedMethod shouldBe HttpMethod.Post
        capturedPath shouldBe "/sync/ratings"
    }

    @Test
    fun `should include rating and ids in request body given addRatings is called`() = runTest {
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = """{"added":{"movies":0,"shows":1,"seasons":0,"episodes":0},""" +
                    """"not_found":{"movies":[],"shows":[],"seasons":[],"episodes":[]}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.addRatings(
            TraktRatingsRequest(
                shows = listOf(TraktShowRatingItem(rating = 8, ids = TraktShowIds(traktId = 1, tmdbId = 296))),
                seasons = listOf(TraktSeasonRatingItem(rating = 7, ids = TraktSeasonIds(traktId = 2, tmdbId = 297))),
                episodes = listOf(TraktEpisodeRatingItem(rating = 6, ids = TraktEpisodeIds(traktId = 3, tmdbId = 298))),
            ),
        )

        capturedBody shouldContain "\"rating\": 8"
        capturedBody shouldContain "\"rating\": 7"
        capturedBody shouldContain "\"rating\": 6"
        capturedBody shouldContain "296"
        capturedBody shouldContain "297"
        capturedBody shouldContain "298"
    }

    @Test
    fun `should return Success with added counts given addRatings succeeds`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """{"added":{"movies":0,"shows":1,"seasons":2,"episodes":3},""" +
                    """"not_found":{"movies":[],"shows":[],"seasons":[],"episodes":[]}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.addRatings(
            TraktRatingsRequest(shows = listOf(TraktShowRatingItem(rating = 8, ids = TraktShowIds(traktId = 1, tmdbId = 296)))),
        )

        val success = result.shouldBeInstanceOf<ApiResponse.Success<TraktAddRatingsResponse>>()
        success.body.added.shows shouldBe 1
        success.body.added.seasons shouldBe 2
        success.body.added.episodes shouldBe 3
    }

    @Test
    fun `should use POST method and correct path given removeRatings is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = """{"deleted":{"movies":0,"shows":1,"seasons":0,"episodes":0},""" +
                    """"not_found":{"movies":[],"shows":[],"seasons":[],"episodes":[]}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.removeRatings(
            TraktRemoveRatingsRequest(shows = listOf(TraktShowRatingIdItem(ids = TraktShowIds(traktId = 1, tmdbId = 296)))),
        )

        capturedMethod shouldBe HttpMethod.Post
        capturedPath shouldBe "/sync/ratings/remove"
    }

    @Test
    fun `should include ids without rating in request body given removeRatings is called`() = runTest {
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                content = """{"deleted":{"movies":0,"shows":1,"seasons":1,"episodes":1},""" +
                    """"not_found":{"movies":[],"shows":[],"seasons":[],"episodes":[]}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.removeRatings(
            TraktRemoveRatingsRequest(
                shows = listOf(TraktShowRatingIdItem(ids = TraktShowIds(traktId = 1, tmdbId = 296))),
                seasons = listOf(TraktSeasonRatingIdItem(ids = TraktSeasonIds(traktId = 2, tmdbId = 297))),
                episodes = listOf(TraktEpisodeRatingIdItem(ids = TraktEpisodeIds(traktId = 3, tmdbId = 298))),
            ),
        )

        capturedBody shouldContain "296"
        capturedBody shouldContain "297"
        capturedBody shouldContain "298"
        capturedBody shouldNotContain "\"rating\""
    }

    @Test
    fun `should return Success with deleted counts given removeRatings succeeds`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """{"deleted":{"movies":0,"shows":1,"seasons":2,"episodes":3},""" +
                    """"not_found":{"movies":[],"shows":[],"seasons":[],"episodes":[]}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.removeRatings(
            TraktRemoveRatingsRequest(shows = listOf(TraktShowRatingIdItem(ids = TraktShowIds(traktId = 1, tmdbId = 296)))),
        )

        val success = result.shouldBeInstanceOf<ApiResponse.Success<TraktRemoveRatingsResponse>>()
        success.body.deleted.shows shouldBe 1
        success.body.deleted.seasons shouldBe 2
        success.body.deleted.episodes shouldBe 3
    }

    @Test
    fun `should use GET and correct path given getUserShowRatings is called`() = runTest {
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

        dataSource.getUserShowRatings()

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/sync/ratings/shows"
    }

    @Test
    fun `should return Success with parsed items given getUserShowRatings succeeds`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """[{"rated_at":"2014-09-01T09:10:11.000Z","rating":10,"type":"show",""" +
                    """"show":{"title":"Breaking Bad","year":2008,""" +
                    """"ids":{"trakt":1,"slug":"breaking-bad","tmdb":1396,"imdb":"tt0903747"}}}]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getUserShowRatings()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<TraktUserRatingItem>>>()
        success.body shouldHaveSize 1
        success.body.first().rating shouldBe 10
        success.body.first().show.title shouldBe "Breaking Bad"
        success.body.first().show.ids.traktId shouldBe 1
        success.body.first().show.ids.tmdbId shouldBe 1396
    }

    @Test
    fun `should use GET and correct path given getShowCommunityRating is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = """{"rating":9.0,"votes":51065,"distribution":{"1":123,"10":3000}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getShowCommunityRating(traktId = 1)

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/shows/1/ratings"
    }

    @Test
    fun `should return Success with rating votes and distribution given getShowCommunityRating succeeds`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """{"rating":9.0,"votes":51065,"distribution":{"1":123,"10":3000}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        val result = dataSource.getShowCommunityRating(traktId = 1)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<TraktRatingResponse>>()
        success.body.rating shouldBe 9.0
        success.body.votes shouldBe 51065
        success.body.distribution["10"] shouldBe 3000
    }

    @Test
    fun `should use GET and correct path given getSeasonCommunityRating is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = """{"rating":8.5,"votes":200,"distribution":{"1":1,"10":100}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getSeasonCommunityRating(traktId = 1, seasonNumber = 2)

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/shows/1/seasons/2/ratings"
    }

    @Test
    fun `should use GET and correct path given getEpisodeCommunityRating is called`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedPath: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedPath = request.url.encodedPath
            respond(
                content = """{"rating":8.9,"votes":50,"distribution":{"1":1,"10":30}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dataSource = createDataSource(engine)

        dataSource.getEpisodeCommunityRating(traktId = 1, seasonNumber = 2, episodeNumber = 3)

        capturedMethod shouldBe HttpMethod.Get
        capturedPath shouldBe "/shows/1/seasons/2/episodes/3/ratings"
    }

    @Test
    fun `should return Success given getShowCommunityRating is called without authentication`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """{"rating":9.0,"votes":51065,"distribution":{"1":123,"10":3000}}""",
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
        val dataSource = DefaultTraktRatingsRemoteDataSource(httpClient = client)

        val result = dataSource.getShowCommunityRating(traktId = 1)

        result.shouldBeInstanceOf<ApiResponse.Success<TraktRatingResponse>>()
    }

    @Test
    fun `should return Unauthenticated given getUserShowRatings is called without authentication`() = runTest {
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
                isAuthenticated = { false }
            }
        }
        client.attributes.put(IsAuthenticated) { false }
        val dataSource = DefaultTraktRatingsRemoteDataSource(httpClient = client)

        val result = dataSource.getUserShowRatings()

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }
}
