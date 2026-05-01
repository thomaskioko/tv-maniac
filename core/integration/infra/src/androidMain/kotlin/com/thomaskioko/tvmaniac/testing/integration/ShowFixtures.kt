package com.thomaskioko.tvmaniac.testing.integration

import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

public data class ShowFixture(
    val traktId: Long,
    val tmdbId: Long,
    val slug: String,
    val imdb: String,
    val title: String,
    val year: Int,
)

/**
 * Parses a Trakt watchlist response body (array of `{ "show": { "ids": ..., "title": ..., "year": ... } }`)
 * into a list of [ShowFixture]. The returned ids/metadata are the source of truth for any
 * per-show stub registered via [stubShow].
 */
public fun showFixtures(json: String): List<ShowFixture> {
    return Json.parseToJsonElement(json).jsonArray.map { entry ->
        val show = entry.jsonObject.getValue("show").jsonObject
        val ids = show.getValue("ids").jsonObject
        ShowFixture(
            traktId = ids.getValue("trakt").jsonPrimitive.long,
            tmdbId = ids.getValue("tmdb").jsonPrimitive.long,
            slug = ids.getValue("slug").jsonPrimitive.content,
            imdb = ids.getValue("imdb").jsonPrimitive.content,
            title = show.getValue("title").jsonPrimitive.content,
            year = show.getValue("year").jsonPrimitive.int,
        )
    }
}

/**
 * Trakt id of the show whose canonical fixtures (details, seasons, episodes) are the source of
 * truth for the test data. Other followed shows reuse this canonical body via per-show stubs that
 * rewrite ids; their season-episode endpoints are stubbed empty to keep `episode.id` collisions out
 * of the database.
 */
public const val CANONICAL_SHOW_TRAKT_ID: Long = 1388L

private const val MAX_STUBBED_SEASON_NUMBER = 5

/**
 * Registers per-show exact-path stubs for [show] on this [MockEngineHandler]:
 * - Trakt `/shows/{traktId}` returns the canonical `Endpoints.Trakt.ShowDetails` body with ids,
 *   title, year, and `first_aired` rewritten to match [show].
 * - Trakt `/shows/{traktId}/seasons` returns the canonical `Endpoints.Trakt.ShowSeasons` body
 *   with each season's `ids.trakt` rewritten to a per-show value so seasons don't collide on the
 *   shared PRIMARY KEY when multiple shows are synced from the same fixture.
 * - TMDB `/3/tv/{tmdbId}` returns the canonical `Endpoints.Tmdb.ShowDetails` body with the root
 *   `id` rewritten.
 * - TMDB `/3/tv/{tmdbId}/watch/providers` returns the canonical
 *   `Endpoints.Tmdb.WatchProviders` body with the root `id` rewritten.
 *
 * For non-canonical shows, also registers `/shows/{traktId}/seasons/{0..MAX}` stubs that return
 * empty arrays. Without these, the `Endpoints.Trakt.ShowSeasonEpisodesS{1,2}` patterns serve the
 * canonical Breaking Bad episodes for every show, and `INSERT OR REPLACE INTO episode (id, ...)`
 * leaves only the last-synced show owning the episode rows.
 *
 * Pattern fallbacks (`Endpoints.Trakt.ShowDetails`, etc.) registered earlier are overridden by
 * these exact-path stubs because `MockEngineHandler.handle()` iterates `stubs.asReversed()`.
 */
public fun MockEngineHandler.stubShow(show: ShowFixture) {
    val traktDetailsTemplate = FixtureLoader.load(Endpoints.Trakt.ShowDetails.successFixture)
    val traktSeasonsTemplate = FixtureLoader.load(Endpoints.Trakt.ShowSeasons.successFixture)
    val tmdbDetailsTemplate = FixtureLoader.load(Endpoints.Tmdb.ShowDetails.successFixture)
    val tmdbProvidersTemplate = FixtureLoader.load(Endpoints.Tmdb.WatchProviders.successFixture)

    stub(
        path = "/shows/${show.traktId}",
        body = rewriteTraktShowIds(traktDetailsTemplate, show),
    )
    stub(
        path = "/shows/${show.traktId}/seasons",
        body = rewriteTraktSeasonIds(traktSeasonsTemplate, show),
    )
    stub(
        path = "/3/tv/${show.tmdbId}",
        body = rewriteRootId(tmdbDetailsTemplate, show.tmdbId),
    )
    stub(
        path = "/3/tv/${show.tmdbId}/watch/providers",
        body = rewriteRootId(tmdbProvidersTemplate, show.tmdbId),
    )

    if (show.traktId != CANONICAL_SHOW_TRAKT_ID) {
        for (seasonNumber in 0..MAX_STUBBED_SEASON_NUMBER) {
            stub(
                path = "/shows/${show.traktId}/seasons/$seasonNumber",
                body = "[]",
            )
        }
    }
}

private fun rewriteTraktShowIds(template: String, show: ShowFixture): String {
    val obj = Json.parseToJsonElement(template).jsonObject
    val updatedIds = JsonObject(
        obj.getValue("ids").jsonObject.toMutableMap().apply {
            this["trakt"] = JsonPrimitive(show.traktId)
            this["tmdb"] = JsonPrimitive(show.tmdbId)
            this["slug"] = JsonPrimitive(show.slug)
            this["imdb"] = JsonPrimitive(show.imdb)
        },
    )
    val updated = JsonObject(
        obj.toMutableMap().apply {
            this["ids"] = updatedIds
            this["title"] = JsonPrimitive(show.title)
            this["year"] = JsonPrimitive(show.year)
            this["first_aired"] = JsonPrimitive("${show.year}-01-01T00:00:00.000Z")
        },
    )
    return Json.encodeToString(JsonObject.serializer(), updated)
}

private fun rewriteRootId(template: String, id: Long): String {
    val obj = Json.parseToJsonElement(template).jsonObject
    val updated = JsonObject(obj.toMutableMap().apply { this["id"] = JsonPrimitive(id) })
    return Json.encodeToString(JsonObject.serializer(), updated)
}

private fun rewriteTraktSeasonIds(template: String, show: ShowFixture): String {
    val seasons = Json.parseToJsonElement(template).jsonArray
    val rewritten = kotlinx.serialization.json.JsonArray(
        seasons.map { season ->
            val seasonObj = season.jsonObject
            val seasonNumber = seasonObj.getValue("number").jsonPrimitive.int
            val perShowSeasonId = show.traktId * 100 + seasonNumber
            val updatedIds = JsonObject(
                seasonObj.getValue("ids").jsonObject.toMutableMap().apply {
                    this["trakt"] = JsonPrimitive(perShowSeasonId)
                },
            )
            JsonObject(seasonObj.toMutableMap().apply { this["ids"] = updatedIds })
        },
    )
    return Json.encodeToString(kotlinx.serialization.json.JsonArray.serializer(), rewritten)
}
