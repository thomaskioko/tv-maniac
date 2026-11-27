package com.thomaskioko.tvmaniac.testing.integration

import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test

internal class ShowFixturesTest {

    @Test
    fun `should give episode ids that fit in an int given a show other than breaking bad`() = runTest {
        val handler = MockEngineHandler()
        val client = HttpClient(MockEngine { request -> handler.handle(this, request) })
        val canonicalEpisodeIds = canonicalEpisodeIds()
        val shows = listOf(
            Endpoints.Trakt.UsersMeWatchlistShows.successFixture,
            Endpoints.Trakt.SyncWatchedShows.successFixture,
            Endpoints.Trakt.SyncProgressUpNextNitro.successFixture,
        ).flatMap { showFixtures(FixtureLoader.load(it)) }
            .filter { it.tmdbId != CANONICAL_SHOW_TMDB_ID }
        shows.map { it.tmdbId }.contains(UNSTARTED_SHOW_TMDB_ID) shouldBe true
        shows.forEach { handler.stubShow(it) }

        shows.forEach { show ->
            listOf(1, 2).forEach { seasonNumber ->
                val body = client.get("https://api.themoviedb.org/3/tv/${show.tmdbId}/season/$seasonNumber").bodyAsText()
                val episodeIds = Json.parseToJsonElement(body).jsonObject
                    .getValue("episodes").jsonArray
                    .map { it.jsonObject.getValue("id").jsonPrimitive.int }
                episodeIds.shouldNotBeEmpty()
                episodeIds shouldNotContainAnyOf canonicalEpisodeIds
            }
        }
    }

    private fun canonicalEpisodeIds(): List<Int> = listOf(
        Endpoints.Tmdb.SeasonDetailsS1.successFixture,
        Endpoints.Tmdb.SeasonDetailsS2.successFixture,
    ).flatMap { fixture ->
        Json.parseToJsonElement(FixtureLoader.load(fixture)).jsonObject
            .getValue("episodes").jsonArray
            .map { it.jsonObject.getValue("id").jsonPrimitive.int }
    }

    private companion object {
        private const val UNSTARTED_SHOW_TMDB_ID = 555555L
    }
}
