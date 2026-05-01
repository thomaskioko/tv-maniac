package com.thomaskioko.tvmaniac.testing.integration

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

/**
 * Generic empty JSON array (`[]`) fixture. Shared "no results" body reused across endpoints
 * such as `/search` and `/calendars/my/shows/...`. Not bound to a specific endpoint, so it lives
 * outside the [Endpoints] catalog.
 */
public const val EMPTY_ARRAY_FIXTURE: String = "empty_array.json"

/**
 * A stubbable Trakt or TMDB endpoint. Carries both the happy-path and error fixture paths so
 * callers never type either string.
 *
 * Conventions:
 * - Each endpoint has a folder under `core/integration/infra/.../resources/fixtures/<vendor>/`
 *   containing `success.json` and `error.json`.
 * - [successFixture] is served when the test asks for a success status (HTTP 2xx).
 * - [errorFixture] is served when the test asks for an error status.
 */
public sealed interface Endpoint {
    /** Fixture file served for success outcomes. */
    public val successFixture: String

    /** Fixture file served for error outcomes. */
    public val errorFixture: String

    /** Endpoint matched by exact path equality. */
    public sealed interface Exact : Endpoint {
        /** HTTP path, beginning with `/`. */
        public val path: String
    }

    /** Endpoint matched by regex against the request path. */
    public sealed interface Pattern : Endpoint {
        /** Regex pattern. Must match the entire path. */
        public val pathRegex: String
    }
}

/**
 * Stubs [endpoint] with the fixture for [status] (defaults to success). Hides both the HTTP
 * path and the fixture file from callers.
 */
public fun MockEngineHandler.stubEndpoint(
    endpoint: Endpoint.Exact,
    status: HttpStatusCode = HttpStatusCode.OK,
    method: HttpMethod = HttpMethod.Get,
) {
    val fixture = if (status.isSuccess()) endpoint.successFixture else endpoint.errorFixture
    stubFixture(method, endpoint.path, fixture, status)
}

/**
 * Stubs [endpoint] (a regex-matched endpoint) with the fixture for [status] (defaults to
 * success).
 */
public fun MockEngineHandler.stubEndpoint(
    endpoint: Endpoint.Pattern,
    status: HttpStatusCode = HttpStatusCode.OK,
    method: HttpMethod = HttpMethod.Get,
) {
    val fixture = if (status.isSuccess()) endpoint.successFixture else endpoint.errorFixture
    stubPatternFixture(method, endpoint.pathRegex, fixture, status)
}

/**
 * Concrete [Endpoint.Exact] used by slug- or date-bound factories in [Endpoints]. Internal so
 * callers go through the catalog factories instead of constructing endpoints ad-hoc.
 */
internal data class ExactEndpoint(
    override val path: String,
    override val successFixture: String,
    override val errorFixture: String,
) : Endpoint.Exact

/**
 * Catalog of every Trakt and TMDB endpoint the integration test suite stubs. Adding a new
 * endpoint: drop a folder under `fixtures/` containing `success.json` and `error.json`, then
 * add a constant or factory here.
 */
public object Endpoints {

    /** Trakt API endpoints. */
    public object Trakt {
        public object ShowsTrending : Endpoint.Exact {
            override val path: String = "/shows/trending"
            override val successFixture: String = "trakt/shows/trending/success.json"
            override val errorFixture: String = "trakt/shows/trending/error.json"
        }

        public object ShowsPopular : Endpoint.Exact {
            override val path: String = "/shows/popular"
            override val successFixture: String = "trakt/shows/popular/success.json"
            override val errorFixture: String = "trakt/shows/popular/error.json"
        }

        public object ShowsFavoritedWeekly : Endpoint.Exact {
            override val path: String = "/shows/favorited/weekly"
            override val successFixture: String = "trakt/shows/favorite/success.json"
            override val errorFixture: String = "trakt/shows/favorite/error.json"
        }

        public object GenresShows : Endpoint.Exact {
            override val path: String = "/genres/shows"
            override val successFixture: String = "trakt/genres/success.json"
            override val errorFixture: String = "trakt/genres/error.json"
        }

        public object UsersMe : Endpoint.Exact {
            override val path: String = "/users/me"
            override val successFixture: String = "trakt/users/me/success.json"
            override val errorFixture: String = "trakt/users/me/error.json"
        }

        public object UsersMeWatchlistShows : Endpoint.Exact {
            override val path: String = "/users/me/watchlist/shows"
            override val successFixture: String = "trakt/users/watchlist/success.json"
            override val errorFixture: String = "trakt/users/watchlist/error.json"
        }

        public object SyncLastActivities : Endpoint.Exact {
            override val path: String = "/sync/last_activities"
            override val successFixture: String = "trakt/sync/success.json"
            override val errorFixture: String = "trakt/sync/error.json"
        }

        public object SyncHistory : Endpoint.Exact {
            override val path: String = "/sync/history"
            override val successFixture: String = "trakt/sync/history/success.json"
            override val errorFixture: String = "trakt/sync/history/error.json"
        }

        public object SearchByTmdb : Endpoint.Pattern {
            override val pathRegex: String = "/search/tmdb/\\d+"
            override val successFixture: String = "trakt/search/success.json"
            override val errorFixture: String = "trakt/search/error.json"
        }

        public object Search : Endpoint.Exact {
            override val path: String = "/search"
            override val successFixture: String = "trakt/search/success.json"
            override val errorFixture: String = "trakt/search/error.json"
        }

        public object ShowDetails : Endpoint.Pattern {
            override val pathRegex: String = "/shows/\\d+"
            override val successFixture: String = "trakt/shows/details/success.json"
            override val errorFixture: String = "trakt/shows/details/error.json"
        }

        public object ShowSeasons : Endpoint.Pattern {
            override val pathRegex: String = "/shows/\\d+/seasons"
            override val successFixture: String = "trakt/seasons/success.json"
            override val errorFixture: String = "trakt/seasons/error.json"
        }

        public object ShowSeasonEpisodesS1 : Endpoint.Pattern {
            override val pathRegex: String = "/shows/\\d+/seasons/1"
            override val successFixture: String = "trakt/episodes/season1/success.json"
            override val errorFixture: String = "trakt/episodes/season1/error.json"
        }

        public object ShowSeasonEpisodesS2 : Endpoint.Pattern {
            override val pathRegex: String = "/shows/\\d+/seasons/2"
            override val successFixture: String = "trakt/episodes/season2/success.json"
            override val errorFixture: String = "trakt/episodes/season2/error.json"
        }

        public object ShowPeople : Endpoint.Pattern {
            override val pathRegex: String = "/shows/\\d+/people"
            override val successFixture: String = "trakt/shows/people/success.json"
            override val errorFixture: String = "trakt/shows/people/error.json"
        }

        public object ShowRelated : Endpoint.Pattern {
            override val pathRegex: String = "/shows/\\d+/related"
            override val successFixture: String = "trakt/shows/related/success.json"
            override val errorFixture: String = "trakt/shows/related/error.json"
        }

        public object ShowVideos : Endpoint.Pattern {
            override val pathRegex: String = "/shows/\\d+/videos"
            override val successFixture: String = "trakt/shows/videos/success.json"
            override val errorFixture: String = "trakt/shows/videos/error.json"
        }

        public object ShowProgressWatched : Endpoint.Pattern {
            override val pathRegex: String = "/shows/\\d+/progress/watched"
            override val successFixture: String = "trakt/shows/progress/watched/success.json"
            override val errorFixture: String = "trakt/shows/progress/watched/error.json"
        }

        /** `/users/{slug}/stats` — slug-bound. */
        public fun userStats(slug: String): Endpoint.Exact = ExactEndpoint(
            path = "/users/$slug/stats",
            successFixture = "trakt/users/stats/success.json",
            errorFixture = "trakt/users/stats/error.json",
        )

        /** `/users/{slug}/lists` — slug-bound. */
        public fun userLists(slug: String): Endpoint.Exact = ExactEndpoint(
            path = "/users/$slug/lists",
            successFixture = "trakt/users/lists/success.json",
            errorFixture = "trakt/users/lists/error.json",
        )

        /** `POST /users/{slug}/lists` — slug-bound. Creates a new personal list. */
        public fun createList(slug: String): Endpoint.Exact = ExactEndpoint(
            path = "/users/$slug/lists",
            successFixture = "trakt/users/lists/create/success.json",
            errorFixture = "trakt/users/lists/create/error.json",
        )

        /** `POST /users/{slug}/lists/{listId}/items` — slug- and list-bound. Adds a show to a list. */
        public fun addShowToList(slug: String, listId: Long): Endpoint.Exact = ExactEndpoint(
            path = "/users/$slug/lists/$listId/items",
            successFixture = "trakt/users/lists/items/add/success.json",
            errorFixture = "trakt/users/lists/items/add/error.json",
        )

        /** `POST /users/{slug}/lists/{listId}/items/remove` — slug- and list-bound. Removes a show from a list. */
        public fun removeShowFromList(slug: String, listId: Long): Endpoint.Exact = ExactEndpoint(
            path = "/users/$slug/lists/$listId/items/remove",
            successFixture = "trakt/users/lists/items/remove/success.json",
            errorFixture = "trakt/users/lists/items/remove/error.json",
        )

        /** `/calendars/my/shows/{weekStart}/{days}` — date-bound. */
        public fun calendar(weekStart: String, days: Int): Endpoint.Exact = ExactEndpoint(
            path = "/calendars/my/shows/$weekStart/$days",
            successFixture = "trakt/calendar/success.json",
            errorFixture = "trakt/calendar/error.json",
        )

        /**
         * Variant of `/shows/{showTraktId}/progress/watched` returning the progress state after
         * the pilot has been marked as watched (pilot becomes `last_episode`, episode 2 becomes
         * `next_episode`). Used to verify UpNext refresh after `markEpisodeAsWatched`.
         */
        public fun showProgressAfterPilotWatched(showTraktId: Long): Endpoint.Exact = ExactEndpoint(
            path = "/shows/$showTraktId/progress/watched",
            successFixture = "trakt/shows/progress/refreshed/success.json",
            errorFixture = "trakt/shows/progress/refreshed/error.json",
        )
    }

    /** TMDB API endpoints (paths begin with `/3/`). */
    public object Tmdb {
        public object DiscoverTv : Endpoint.Exact {
            override val path: String = "/3/discover/tv"
            override val successFixture: String = "tmdb/discover/success.json"
            override val errorFixture: String = "tmdb/discover/error.json"
        }

        public object ShowDetails : Endpoint.Pattern {
            override val pathRegex: String = "/3/tv/\\d+"
            override val successFixture: String = "tmdb/details/success.json"
            override val errorFixture: String = "tmdb/details/error.json"
        }

        public object Credits : Endpoint.Pattern {
            override val pathRegex: String = "/3/tv/\\d+/credits"
            override val successFixture: String = "tmdb/credits/success.json"
            override val errorFixture: String = "tmdb/credits/error.json"
        }

        public object SeasonDetails : Endpoint.Pattern {
            override val pathRegex: String = "/3/tv/\\d+/season/\\d+"
            override val successFixture: String = "tmdb/details/success.json"
            override val errorFixture: String = "tmdb/details/error.json"
        }

        public object WatchProviders : Endpoint.Pattern {
            override val pathRegex: String = "/3/tv/\\d+/watch/providers"
            override val successFixture: String = "tmdb/watchproviders/success.json"
            override val errorFixture: String = "tmdb/watchproviders/error.json"
        }
    }

    /**
     * Every endpoint instance the catalog exposes (objects only — slug- and date-bound factories
     * aren't enumerated). Used by `EndpointsCatalogTest` to verify both fixture files exist on
     * the classpath.
     */
    public val all: List<Endpoint> = listOf(
        Trakt.ShowsTrending,
        Trakt.ShowsPopular,
        Trakt.ShowsFavoritedWeekly,
        Trakt.GenresShows,
        Trakt.UsersMe,
        Trakt.UsersMeWatchlistShows,
        Trakt.SyncLastActivities,
        Trakt.SyncHistory,
        Trakt.SearchByTmdb,
        Trakt.Search,
        Trakt.ShowDetails,
        Trakt.ShowSeasons,
        Trakt.ShowSeasonEpisodesS1,
        Trakt.ShowSeasonEpisodesS2,
        Trakt.ShowPeople,
        Trakt.ShowRelated,
        Trakt.ShowVideos,
        Trakt.ShowProgressWatched,
        Tmdb.DiscoverTv,
        Tmdb.ShowDetails,
        Tmdb.Credits,
        Tmdb.SeasonDetails,
        Tmdb.WatchProviders,
    )
}
