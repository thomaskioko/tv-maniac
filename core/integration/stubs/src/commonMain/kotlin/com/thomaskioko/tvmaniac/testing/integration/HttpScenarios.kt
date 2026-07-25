package com.thomaskioko.tvmaniac.testing.integration

import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

public const val TEST_PROFILE_SLUG: String = "integration-test-user"
public const val TEST_TODAY: String = "2026-04-19"
public const val TEST_NEXT_WEEK: String = "2026-04-26"

/** Trakt id of the list returned by `trakt/users/lists/create/success.json`. */
public const val TEST_CREATED_LIST_TRAKT_ID: Long = 99887766L

/** Name of the list returned by `trakt/users/lists/create/success.json`. */
public const val TEST_CREATED_LIST_NAME: String = "Watch Later"

/**
 * Every stub registration that is purely HTTP, so Android, the JVM and iOS answer test requests
 * from one set of saved responses instead of three different backends.
 *
 * Methods name their provider rather than taking a `SyncProviderSource`. That keeps this module
 * free of project dependencies, which matters because `ios-framework` has to depend on it and
 * `data/account-manager/api` would pull SQLDelight along with the enum. Callers that already hold
 * a provider value do the branch themselves.
 *
 * Anything that mutates a dependency graph fake, drives the UI, or seeds credentials stays with
 * the caller; this class only ever touches [mockHandler].
 */
public class HttpScenarios(private val mockHandler: MockEngineHandler) {

    /**
     * Public data reachable from Discover regardless of login state: the Trakt and TMDB list
     * endpoints plus the per-show graph. Account-specific endpoints belong to the authenticated
     * methods below, not here.
     */
    public fun stubBrowseGraph() {
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowsFavoritedWeekly)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.GenresShows)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowsTrending)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowsPopular)
        mockHandler.stubEndpoint(Endpoints.Tmdb.DiscoverTv)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.SearchByTmdb)

        // Per-show public-catalog endpoints — single canonical fixture for any trakt id.
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowDetails)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowSeasons)
        // Per-season episodes: catch-all returns empty so unstubbed seasons don't re-write
        // episode rows. The per-season stubs registered after win under last-registered-first-match
        // ordering. (Catalog has no entry for the catch-all because the empty-array fixture is
        // shared across endpoints and isn't tied to one resource folder.)
        mockHandler.stubPatternFixture(pathRegex = "/shows/\\d+/seasons/\\d+", fixturePath = "empty_array.json")
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowSeasonEpisodesS1)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowSeasonEpisodesS2)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowPeople)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowRelated)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowVideos)

        // Per-show TMDB endpoints — single canonical fixture for any tmdb id.
        mockHandler.stubEndpoint(Endpoints.Tmdb.ShowDetails)
        mockHandler.stubEndpoint(Endpoints.Tmdb.Credits)
        mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetails)
        mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetailsS1)
        mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetailsS2)
        mockHandler.stubEndpoint(Endpoints.Tmdb.WatchProviders)
    }

    public fun stubSearch(query: String) {
        mockHandler.stubSearchByQuery(query)
    }

    public fun stubEmptySearch() {
        mockHandler.stubFixture(path = Endpoints.PublicCatalog.Search.path, fixturePath = EMPTY_ARRAY_FIXTURE)
    }

    public fun stubSearchError(query: String) {
        mockHandler.stubSearchByQuery(query, HttpStatusCode.Forbidden)
    }

    public fun stubTraktSyncHistoryPost() {
        mockHandler.stubEndpoint(Endpoints.Trakt.SyncHistory, method = HttpMethod.Post)
    }

    public fun stubSimklSyncHistoryPost() {
        mockHandler.stubEndpoint(Endpoints.Simkl.SyncHistory, method = HttpMethod.Post)
    }

    public fun stubTraktCalendarWeek(weekStart: String = TEST_TODAY, days: Int = 7) {
        mockHandler.stubEndpoint(Endpoints.Trakt.calendar(weekStart, days))
    }

    public fun stubTraktCalendarWeekEmpty(weekStart: String = TEST_TODAY, days: Int = 7) {
        val calendar = Endpoints.Trakt.calendar(weekStart, days)
        mockHandler.stubFixture(path = calendar.path, fixturePath = EMPTY_ARRAY_FIXTURE, host = calendar.host)
    }

    public fun stubTraktCalendarWeekError(
        weekStart: String = TEST_TODAY,
        days: Int = 7,
        status: HttpStatusCode = HttpStatusCode.NotFound,
    ) {
        mockHandler.stubEndpoint(Endpoints.Trakt.calendar(weekStart, days), status)
    }

    public fun stubSimklCalendarFeed() {
        mockHandler.stubEndpoint(Endpoints.Simkl.CalendarTvFeed)
    }

    public fun stubSimklCalendarFeedEmpty() {
        val calendar = Endpoints.Simkl.CalendarTvFeed
        mockHandler.stubFixture(path = calendar.path, fixturePath = EMPTY_ARRAY_FIXTURE, host = calendar.host)
    }

    public fun stubSimklCalendarFeedError(status: HttpStatusCode = HttpStatusCode.NotFound) {
        mockHandler.stubEndpoint(Endpoints.Simkl.CalendarTvFeed, status)
    }

    public fun stubTraktLibrarySync() {
        mockHandler.stubEndpoint(Endpoints.Trakt.SyncLastActivities)
        mockHandler.stubEndpoint(Endpoints.Trakt.SyncWatchedShows)
        mockHandler.stubEndpoint(Endpoints.Trakt.UsersMeWatchlistShows)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowDetails)
        mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowSeasons)
        mockHandler.stubEndpoint(Endpoints.Tmdb.ShowDetails)
        mockHandler.stubEndpoint(Endpoints.Tmdb.WatchProviders)

        stubShowsFrom(Endpoints.Trakt.SyncWatchedShows.successFixture)
    }

    public fun stubTraktWatchlistSync() {
        mockHandler.stubEndpoint(Endpoints.Trakt.SyncProgressUpNextNitro)
        mockHandler.stubEndpoint(Endpoints.Trakt.SyncPlaybackEpisodes)
        mockHandler.stubEndpoint(Endpoints.Trakt.SyncWatchedShows)
        mockHandler.stubEndpoint(Endpoints.Trakt.ShowProgressWatched)
        mockHandler.stubEndpoint(Endpoints.Trakt.UsersHiddenProgressWatched)

        stubShowsFrom(Endpoints.Trakt.SyncProgressUpNextNitro.successFixture)
    }

    public fun stubTraktProfileSync(slug: String = TEST_PROFILE_SLUG) {
        mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe)
        mockHandler.stubEndpoint(Endpoints.Trakt.userStats(slug))
        mockHandler.stubEndpoint(Endpoints.Trakt.userLists(slug))
        mockHandler.stubEndpoint(Endpoints.Trakt.UserListItems)
    }

    public fun stubTraktAddShowToList(listId: Long, slug: String = TEST_PROFILE_SLUG) {
        mockHandler.stubEndpoint(Endpoints.Trakt.addShowToList(slug, listId), method = HttpMethod.Post)
    }

    public fun stubTraktCreateList(slug: String = TEST_PROFILE_SLUG) {
        mockHandler.stubEndpoint(Endpoints.Trakt.createList(slug), method = HttpMethod.Post)
    }

    public fun stubTraktUsersMeUnauthorized() {
        mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe, HttpStatusCode.Unauthorized)
    }

    public fun stubSimklProfileEndpoints() {
        mockHandler.stubEndpoint(endpoint = Endpoints.Simkl.UsersSettings, method = HttpMethod.Post)
        mockHandler.stubEndpoint(endpoint = Endpoints.Simkl.UsersStats, method = HttpMethod.Post)
    }

    public fun stubSimklWatchedHistory() {
        mockHandler.stubEndpoint(Endpoints.Simkl.SyncAllItems)
    }

    public fun stubSimklPlanToWatchWatchlist() {
        mockHandler.stubFixture(
            path = Endpoints.Simkl.SyncAllItems.path,
            fixturePath = "simkl/sync/all-items/start_watching.json",
        )
    }

    public fun stubSimklActivities() {
        mockHandler.stubEndpoint(Endpoints.Simkl.SyncActivities)
    }

    public fun stubSimklUserSettingsUnauthorized() {
        mockHandler.stubEndpoint(Endpoints.Simkl.UsersSettings, HttpStatusCode.Unauthorized)
    }

    /**
     * Trakt's catalog-declared authenticated endpoints plus the account endpoints that carry a
     * slug or a date. Pair with [stubBrowseGraph], the only source of TMDB and Discover coverage.
     */
    public fun stubTraktAuthenticatedEndpoints(
        slug: String = TEST_PROFILE_SLUG,
        today: String = TEST_TODAY,
        nextWeek: String = TEST_NEXT_WEEK,
    ) {
        val endpoints = Endpoints.Trakt.authenticatedEndpoints + listOf(
            Endpoints.Trakt.userStats(slug),
            Endpoints.Trakt.userLists(slug),
            Endpoints.Trakt.calendar(today, 7),
            Endpoints.Trakt.calendar(nextWeek, 7),
        )
        endpoints.forEach { mockHandler.stubEndpoint(it) }

        // Shared empty-array fixture, not tied to one catalog entry, so it isn't a plain Endpoint.
        // Kept as explicit glue, mirroring the seasons catch-all in stubBrowseGraph.
        mockHandler.stubPatternFixture(
            pathRegex = "/users/me/history/shows/\\d+",
            fixturePath = EMPTY_ARRAY_FIXTURE,
        )

        stubShowsFrom(Endpoints.Trakt.SyncWatchedShows.successFixture)
        stubShowsFrom(Endpoints.Trakt.SyncProgressUpNextNitro.successFixture)
    }

    public fun stubSimklAuthenticatedEndpoints() {
        Endpoints.Simkl.authenticatedEndpoints.forEach { mockHandler.stubEndpoint(it) }
    }

    /**
     * Answers `/users/me` with 401 once, then registers the endpoints a successful refresh calls,
     * so the last-registered-wins ordering replays the round-trip.
     */
    public fun stubTraktTokenRefreshEndpoints(slug: String = TEST_PROFILE_SLUG) {
        mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe, HttpStatusCode.Unauthorized)
        mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe)
        mockHandler.stubEndpoint(Endpoints.Trakt.userStats(slug))
        mockHandler.stubEndpoint(Endpoints.Trakt.userLists(slug))
        mockHandler.stubEndpoint(Endpoints.Trakt.UserListItems)
    }

    private fun stubShowsFrom(fixturePath: String) {
        showFixtures(FixtureLoader.load(fixturePath)).forEach { mockHandler.stubShow(it) }
    }
}
