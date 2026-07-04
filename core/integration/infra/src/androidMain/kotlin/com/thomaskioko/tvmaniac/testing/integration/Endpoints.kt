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
 * A stubbable Trakt, Simkl, or TMDB endpoint. Carries both the happy-path and error fixture
 * paths so callers never type either string, plus the production [host] the endpoint is served
 * from so [MockEngineHandler] can route a stub to the right provider.
 *
 * Conventions:
 * - Each endpoint has a folder under `core/integration/infra/.../resources/fixtures/<vendor>/`
 *   containing `success.json` and `error.json`.
 * - [successFixture] is served when the test asks for a success status (HTTP 2xx).
 * - [errorFixture] is served when the test asks for an error status.
 */
public sealed interface Endpoint {
    /** Production host this endpoint is served from, e.g. `api.trakt.tv`. */
    public val host: String

    /** HTTP method this endpoint is served over. Defaults to `GET`. */
    public val method: HttpMethod get() = HttpMethod.Get

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
    stubFixture(method, endpoint.path, fixture, status, endpoint.host)
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
    stubPatternFixture(method, endpoint.pathRegex, fixture, status, endpoint.host)
}

/**
 * Stubs [endpoint] with the fixture for [status] (defaults to success), dispatching to the
 * [Endpoint.Exact] or [Endpoint.Pattern] overload and reading the HTTP method off the endpoint
 * itself. Lets catalog-driven callers (e.g. `Scenarios.stubActiveProvider`) stub a heterogeneous
 * list of endpoints without switching on their matcher kind.
 */
public fun MockEngineHandler.stubEndpoint(
    endpoint: Endpoint,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    when (endpoint) {
        is Endpoint.Exact -> stubEndpoint(endpoint, status, endpoint.method)
        is Endpoint.Pattern -> stubEndpoint(endpoint, status, endpoint.method)
    }
}

/**
 * Concrete [Endpoint.Exact] used by slug- or date-bound factories in [Endpoints]. Internal so
 * callers go through the catalog factories instead of constructing endpoints ad-hoc.
 */
internal data class ExactEndpoint(
    override val host: String,
    override val path: String,
    override val successFixture: String,
    override val errorFixture: String,
    override val method: HttpMethod = HttpMethod.Get,
) : Endpoint.Exact

/**
 * Catalog of every Trakt, Simkl, and TMDB endpoint the integration test suite stubs. Adding a
 * new endpoint: drop a folder under `fixtures/` containing `success.json` and `error.json`, then
 * add a constant or factory here.
 */
public object Endpoints {

    /** Trakt API endpoints. */
    public object Trakt {
        private const val HOST: String = "api.trakt.tv"

        public object UsersMe : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/users/me"
            override val successFixture: String = "trakt/users/me/success.json"
            override val errorFixture: String = "trakt/users/me/error.json"
        }

        public object UsersMeWatchlistShows : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/users/me/watchlist/shows"
            override val successFixture: String = "trakt/users/watchlist/success.json"
            override val errorFixture: String = "trakt/users/watchlist/error.json"
        }

        public object SyncLastActivities : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/last_activities"
            override val successFixture: String = "trakt/sync/success.json"
            override val errorFixture: String = "trakt/sync/error.json"
        }

        /** `POST /sync/history` — uploads a watched episode/show entry. */
        public object SyncHistory : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/history"
            override val method: HttpMethod = HttpMethod.Post
            override val successFixture: String = "trakt/sync/history/success.json"
            override val errorFixture: String = "trakt/sync/history/error.json"
        }

        public object SyncWatchedShows : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/watched/shows"
            override val successFixture: String = "trakt/sync/watched/shows/success.json"
            override val errorFixture: String = "trakt/sync/watched/shows/error.json"
        }

        public object SyncProgressUpNextNitro : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/progress/up_next_nitro"
            override val successFixture: String = "trakt/sync/progress/up_next_nitro/success.json"
            override val errorFixture: String = "trakt/sync/progress/up_next_nitro/error.json"
        }

        public object SyncPlaybackEpisodes : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/playback/episodes"
            override val successFixture: String = "trakt/sync/playback/episodes/success.json"
            override val errorFixture: String = "trakt/sync/playback/episodes/error.json"
        }

        public object UsersHiddenProgressWatched : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/users/hidden/progress_watched"
            override val successFixture: String = "trakt/users/hidden/progress_watched/success.json"
            override val errorFixture: String = "trakt/users/hidden/progress_watched/error.json"
        }

        public object ShowProgressWatched : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/shows/\\d+/progress/watched"
            override val successFixture: String = "trakt/shows/progress/watched/success.json"
            override val errorFixture: String = "trakt/shows/progress/watched/error.json"
        }

        /** `GET /shows/{traktId}/ratings` — community rating, fetched when Show Details opens. */
        public object ShowCommunityRating : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/shows/\\d+/ratings"
            override val successFixture: String = "trakt/shows/ratings/success.json"
            override val errorFixture: String = "trakt/shows/ratings/error.json"
        }

        /** `POST /sync/ratings` — adds a show, season, or episode rating. */
        public object SyncRatingsAdd : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/ratings"
            override val method: HttpMethod = HttpMethod.Post
            override val successFixture: String = "trakt/sync/ratings/success.json"
            override val errorFixture: String = "trakt/sync/ratings/error.json"
        }

        /** `POST /sync/ratings/remove` — removes a show, season, or episode rating. */
        public object SyncRatingsRemove : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/ratings/remove"
            override val method: HttpMethod = HttpMethod.Post
            override val successFixture: String = "trakt/sync/ratings/remove/success.json"
            override val errorFixture: String = "trakt/sync/ratings/remove/error.json"
        }

        /** `GET /sync/ratings/shows` — the user's rated shows, used to reconcile the local user rating. */
        public object SyncRatingsShows : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/ratings/shows"
            override val successFixture: String = "trakt/sync/ratings/shows/success.json"
            override val errorFixture: String = "trakt/sync/ratings/shows/error.json"
        }

        /** `/users/{slug}/stats` — slug-bound. */
        public fun userStats(slug: String): Endpoint.Exact = ExactEndpoint(
            host = HOST,
            path = "/users/$slug/stats",
            successFixture = "trakt/users/stats/success.json",
            errorFixture = "trakt/users/stats/error.json",
        )

        /** `/users/{slug}/lists` — slug-bound. */
        public fun userLists(slug: String): Endpoint.Exact = ExactEndpoint(
            host = HOST,
            path = "/users/$slug/lists",
            successFixture = "trakt/users/lists/success.json",
            errorFixture = "trakt/users/lists/error.json",
        )

        /** `POST /users/{slug}/lists` — slug-bound. Creates a new personal list. */
        public fun createList(slug: String): Endpoint.Exact = ExactEndpoint(
            host = HOST,
            path = "/users/$slug/lists",
            successFixture = "trakt/users/lists/create/success.json",
            errorFixture = "trakt/users/lists/create/error.json",
        )

        /** `GET /users/{slug}/lists/{listId}/items` — slug- and list-bound. Returns items in a list. */
        public object UserListItems : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/users/[^/]+/lists/\\d+/items"
            override val successFixture: String = "trakt/users/lists/items/success.json"
            override val errorFixture: String = "trakt/users/lists/items/error.json"
        }

        /** `POST /users/{slug}/lists/{listId}/items` — slug- and list-bound. Adds a show to a list. */
        public fun addShowToList(slug: String, listId: Long): Endpoint.Exact = ExactEndpoint(
            host = HOST,
            path = "/users/$slug/lists/$listId/items",
            successFixture = "trakt/users/lists/items/add/success.json",
            errorFixture = "trakt/users/lists/items/add/error.json",
        )

        /** `POST /users/{slug}/lists/{listId}/items/remove` — slug- and list-bound. Removes a show from a list. */
        public fun removeShowFromList(slug: String, listId: Long): Endpoint.Exact = ExactEndpoint(
            host = HOST,
            path = "/users/$slug/lists/$listId/items/remove",
            successFixture = "trakt/users/lists/items/remove/success.json",
            errorFixture = "trakt/users/lists/items/remove/error.json",
        )

        /** `/calendars/my/shows/{weekStart}/{days}` — date-bound. */
        public fun calendar(weekStart: String, days: Int): Endpoint.Exact = ExactEndpoint(
            host = HOST,
            path = "/calendars/my/shows/$weekStart/$days",
            successFixture = "trakt/calendar/success.json",
            errorFixture = "trakt/calendar/error.json",
        )

        /**
         * Variant of `/shows/{traktId}/progress/watched` returning the progress state after
         * the pilot has been marked as watched (pilot becomes `last_episode`, episode 2 becomes
         * `next_episode`). Used to verify UpNext refresh after `markEpisodeAsWatched`.
         */
        public fun showProgressAfterPilotWatched(traktId: Long): Endpoint.Exact = ExactEndpoint(
            host = HOST,
            path = "/shows/$traktId/progress/watched",
            successFixture = "trakt/shows/progress/refreshed/success.json",
            errorFixture = "trakt/shows/progress/refreshed/error.json",
        )

        /**
         * Endpoints stubbed by `Scenarios.stubActiveProvider` for an authenticated Trakt session.
         * Add a cross-provider feature's endpoints here, not a new stub helper. Slug- and
         * date-bound factories (`userStats`, `userLists`, `calendar`) aren't listed here since
         * they need test-constant arguments; `Scenarios.stubActiveProvider` binds those and adds
         * them to this list before stubbing.
         */
        public val authenticatedEndpoints: List<Endpoint> = listOf(
            UsersMe,
            UsersMeWatchlistShows,
            SyncLastActivities,
            SyncHistory,
            SyncWatchedShows,
            SyncProgressUpNextNitro,
            SyncPlaybackEpisodes,
            UsersHiddenProgressWatched,
            ShowProgressWatched,
            ShowCommunityRating,
            SyncRatingsAdd,
            SyncRatingsRemove,
            SyncRatingsShows,
            UserListItems,
        )
    }

    /**
     * Public, no-auth catalog and per-show detail endpoints shared by every account provider
     * (and by no account at all). These are served from Trakt's public API but aren't
     * Trakt-account-specific, so they live outside [Trakt] to keep that group limited to
     * authenticated-session endpoints.
     */
    public object PublicCatalog {
        private const val HOST: String = "api.trakt.tv"

        public object ShowsTrending : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/shows/trending"
            override val successFixture: String = "trakt/shows/trending/success.json"
            override val errorFixture: String = "trakt/shows/trending/error.json"
        }

        public object ShowsPopular : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/shows/popular"
            override val successFixture: String = "trakt/shows/popular/success.json"
            override val errorFixture: String = "trakt/shows/popular/error.json"
        }

        public object ShowsFavoritedWeekly : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/shows/favorited/weekly"
            override val successFixture: String = "trakt/shows/favorite/success.json"
            override val errorFixture: String = "trakt/shows/favorite/error.json"
        }

        public object GenresShows : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/genres/shows"
            override val successFixture: String = "trakt/genres/success.json"
            override val errorFixture: String = "trakt/genres/error.json"
        }

        public object SearchByTmdb : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/search/tmdb/\\d+"
            override val successFixture: String = "trakt/search/success.json"
            override val errorFixture: String = "trakt/search/error.json"
        }

        public object Search : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/search"
            override val successFixture: String = "trakt/search/success.json"
            override val errorFixture: String = "trakt/search/error.json"
        }

        public object ShowDetails : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/shows/\\d+"
            override val successFixture: String = "trakt/shows/details/success.json"
            override val errorFixture: String = "trakt/shows/details/error.json"
        }

        public object ShowSeasons : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/shows/\\d+/seasons"
            override val successFixture: String = "trakt/seasons/success.json"
            override val errorFixture: String = "trakt/seasons/error.json"
        }

        public object ShowSeasonEpisodesS1 : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/shows/\\d+/seasons/1"
            override val successFixture: String = "trakt/episodes/season1/success.json"
            override val errorFixture: String = "trakt/episodes/season1/error.json"
        }

        public object ShowSeasonEpisodesS2 : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/shows/\\d+/seasons/2"
            override val successFixture: String = "trakt/episodes/season2/success.json"
            override val errorFixture: String = "trakt/episodes/season2/error.json"
        }

        public object ShowPeople : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/shows/\\d+/people"
            override val successFixture: String = "trakt/shows/people/success.json"
            override val errorFixture: String = "trakt/shows/people/error.json"
        }

        public object ShowRelated : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/shows/\\d+/related"
            override val successFixture: String = "trakt/shows/related/success.json"
            override val errorFixture: String = "trakt/shows/related/error.json"
        }

        public object ShowVideos : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/shows/\\d+/videos"
            override val successFixture: String = "trakt/shows/videos/success.json"
            override val errorFixture: String = "trakt/shows/videos/error.json"
        }
    }

    public object Simkl {
        private const val HOST: String = "api.simkl.com"

        /** Host backing the Simkl calendar feed, served separately from the main Simkl API. */
        private const val DATA_HOST: String = "data.simkl.in"

        /** `POST /users/settings` — Simkl serves this read endpoint over POST. */
        public object UsersSettings : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/users/settings"
            override val method: HttpMethod = HttpMethod.Post
            override val successFixture: String = "simkl/users/settings/success.json"
            override val errorFixture: String = "simkl/users/settings/error.json"
        }

        /** `POST /users/{userId}/stats` — Simkl serves this read endpoint over POST. */
        public object UsersStats : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/users/[^/]+/stats"
            override val method: HttpMethod = HttpMethod.Post
            override val successFixture: String = "simkl/users/stats/success.json"
            override val errorFixture: String = "simkl/users/stats/error.json"
        }

        public object SyncAllItems : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/all-items/shows"
            override val successFixture: String = "simkl/sync/all-items/success.json"
            override val errorFixture: String = "simkl/sync/all-items/error.json"
        }

        public object SyncActivities : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/activities"
            override val successFixture: String = "simkl/sync/activities/success.json"
            override val errorFixture: String = "simkl/sync/activities/error.json"
        }

        /**
         * `POST /sync/history` — uploads a watched episode/show entry to Simkl. Mirrors
         * [Trakt.SyncHistory]; same path, different host, so registering both providers' stubs in
         * the same test is safe.
         */
        public object SyncHistory : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/history"
            override val method: HttpMethod = HttpMethod.Post
            override val successFixture: String = "simkl/sync/history/success.json"
            override val errorFixture: String = "simkl/sync/history/error.json"
        }

        /**
         * `GET /tv/{simklId}?extended=full` — show summary, community rating comes from
         * `body.ratings.simkl`. Shares its path shape with no other endpoint in this catalog.
         */
        public object ShowSummary : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/tv/\\d+"
            override val successFixture: String = "simkl/tv/success.json"
            override val errorFixture: String = "simkl/tv/error.json"
        }

        /**
         * `POST /sync/ratings` — adds a show rating. Same path as [Trakt.SyncRatingsAdd] but
         * served from a different host; [MockEngineHandler] matches by host, method, and path,
         * so registering both providers' rating endpoints in the same test is safe.
         */
        public object SyncRatingsAdd : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/ratings"
            override val method: HttpMethod = HttpMethod.Post
            override val successFixture: String = "simkl/sync/ratings/success.json"
            override val errorFixture: String = "simkl/sync/ratings/error.json"
        }

        /** `POST /sync/ratings/remove` — removes a show rating. */
        public object SyncRatingsRemove : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/ratings/remove"
            override val method: HttpMethod = HttpMethod.Post
            override val successFixture: String = "simkl/sync/ratings/remove/success.json"
            override val errorFixture: String = "simkl/sync/ratings/remove/error.json"
        }

        /** `GET /sync/ratings/shows` — the user's rated shows, used to reconcile the local user rating. */
        public object SyncRatingsShows : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/sync/ratings/shows"
            override val successFixture: String = "simkl/sync/ratings/shows/success.json"
            override val errorFixture: String = "simkl/sync/ratings/shows/error.json"
        }

        /** `GET /calendar/tv.json` — served from [DATA_HOST], not [HOST]. */
        public object CalendarTvFeed : Endpoint.Exact {
            override val host: String = DATA_HOST
            override val path: String = "/calendar/tv.json"
            override val successFixture: String = "simkl/calendar/tv.json"
            override val errorFixture: String = EMPTY_ARRAY_FIXTURE
        }

        /**
         * Endpoints stubbed by `Scenarios.stubActiveProvider` for an authenticated Simkl session.
         * Add a cross-provider feature's endpoints here, not a new stub helper.
         */
        public val authenticatedEndpoints: List<Endpoint> = listOf(
            UsersSettings,
            UsersStats,
            SyncAllItems,
            SyncActivities,
            SyncHistory,
            ShowSummary,
            CalendarTvFeed,
            SyncRatingsAdd,
            SyncRatingsRemove,
            SyncRatingsShows,
        )
    }

    /** TMDB API endpoints (paths begin with `/3/`). */
    public object Tmdb {
        private const val HOST: String = "api.themoviedb.org"

        public object DiscoverTv : Endpoint.Exact {
            override val host: String = HOST
            override val path: String = "/3/discover/tv"
            override val successFixture: String = "tmdb/discover/success.json"
            override val errorFixture: String = "tmdb/discover/error.json"
        }

        public object ShowDetails : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/3/tv/\\d+"
            override val successFixture: String = "tmdb/details/success.json"
            override val errorFixture: String = "tmdb/details/error.json"
        }

        public object Credits : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/3/tv/\\d+/credits"
            override val successFixture: String = "tmdb/credits/success.json"
            override val errorFixture: String = "tmdb/credits/error.json"
        }

        public object SeasonDetails : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/3/tv/\\d+/season/\\d+"
            override val successFixture: String = "tmdb/season_details/empty/success.json"
            override val errorFixture: String = "tmdb/season_details/empty/error.json"
        }

        public object SeasonDetailsS1 : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/3/tv/\\d+/season/1"
            override val successFixture: String = "tmdb/season_details/season1/success.json"
            override val errorFixture: String = "tmdb/season_details/season1/error.json"
        }

        public object SeasonDetailsS2 : Endpoint.Pattern {
            override val host: String = HOST
            override val pathRegex: String = "/3/tv/\\d+/season/2"
            override val successFixture: String = "tmdb/season_details/season2/success.json"
            override val errorFixture: String = "tmdb/season_details/season2/error.json"
        }

        public object WatchProviders : Endpoint.Pattern {
            override val host: String = HOST
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
        PublicCatalog.ShowsTrending,
        PublicCatalog.ShowsPopular,
        PublicCatalog.ShowsFavoritedWeekly,
        PublicCatalog.GenresShows,
        Trakt.UsersMe,
        Trakt.UsersMeWatchlistShows,
        Trakt.SyncLastActivities,
        Trakt.SyncHistory,
        Trakt.SyncWatchedShows,
        PublicCatalog.SearchByTmdb,
        PublicCatalog.Search,
        PublicCatalog.ShowDetails,
        PublicCatalog.ShowSeasons,
        PublicCatalog.ShowSeasonEpisodesS1,
        PublicCatalog.ShowSeasonEpisodesS2,
        PublicCatalog.ShowPeople,
        PublicCatalog.ShowRelated,
        PublicCatalog.ShowVideos,
        Trakt.ShowProgressWatched,
        Trakt.ShowCommunityRating,
        Trakt.SyncRatingsAdd,
        Trakt.SyncRatingsRemove,
        Trakt.SyncRatingsShows,
        Tmdb.DiscoverTv,
        Tmdb.ShowDetails,
        Tmdb.Credits,
        Tmdb.SeasonDetails,
        Tmdb.WatchProviders,
        Simkl.UsersSettings,
        Simkl.UsersStats,
        Simkl.SyncAllItems,
        Simkl.SyncActivities,
        Simkl.SyncHistory,
        Simkl.ShowSummary,
        Simkl.SyncRatingsAdd,
        Simkl.SyncRatingsRemove,
        Simkl.SyncRatingsShows,
        Simkl.CalendarTvFeed,
    )
}
