package com.thomaskioko.tvmaniac.app.test.compose.stubs

import com.thomaskioko.tvmaniac.app.test.TestAppComponent
import com.thomaskioko.tvmaniac.app.test.compose.robot.RootRobot
import com.thomaskioko.tvmaniac.testing.integration.EMPTY_ARRAY_FIXTURE
import com.thomaskioko.tvmaniac.testing.integration.Endpoints
import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import com.thomaskioko.tvmaniac.testing.integration.showFixtures
import com.thomaskioko.tvmaniac.testing.integration.stubEndpoint
import com.thomaskioko.tvmaniac.testing.integration.stubSearchByQuery
import com.thomaskioko.tvmaniac.testing.integration.stubShow
import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

internal const val TEST_ACCESS_TOKEN: String = "test-access"
internal const val TEST_REFRESH_TOKEN: String = "test-refresh"
internal const val TEST_PROFILE_SLUG: String = "integration-test-user"
internal const val TEST_TODAY: String = "2026-04-19"

/** Trakt id of the list returned by `trakt/users/lists/create/success.json`. */
internal const val TEST_CREATED_LIST_TRAKT_ID: Long = 99887766L

/** Name of the list returned by `trakt/users/lists/create/success.json`. */
internal const val TEST_CREATED_LIST_NAME: String = "Watch Later"
internal const val TEST_NEXT_WEEK: String = "2026-04-26"

internal class Scenarios(
    private val mockHandler: MockEngineHandler,
    private val graph: TestAppComponent,
    private val rootRobot: RootRobot,
) {
    val auth: Auth = Auth()
    val discover: Discover = Discover()
    val profile: Profile = Profile()
    val library: Library = Library()
    val search: Search = Search()
    val calendar: Calendar = Calendar()
    val upNext: UpNext = UpNext()
    val traktLists: TraktLists = TraktLists()

    fun signInAndDismissRationale() {
        auth.stubLoggedInUser()
        profile.stubProfileSyncEndpoints()
        rootRobot.dismissNotificationRationale()
    }

    fun stubAuthenticatedSync() {
        auth.stubLoggedInUser()
        discover.stubBrowseGraph()
        library.stubLibrarySyncEndpoints()
        profile.stubProfileSyncEndpoints()
        calendar.stubWeek()
        calendar.stubWeek(weekStart = TEST_NEXT_WEEK)
    }

    fun stubUsersMeUnauthorized() {
        mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe, HttpStatusCode.Unauthorized)
    }

    /**
     * Registers OAuth WebView callback so that next `clickSignInButton()` lazily wires profile sync
     * endpoints, library sync endpoints, and flips fake auth state to LOGGED_IN. Mirrors live
     * OAuth round-trip without pre-stubbing LOGGED_IN in `@Before`, so DefaultRootPresenter's
     * auth-state collector still observes real LOGGED_OUT to LOGGED_IN transition.
     */
    fun stubAuthenticatedSyncOnSignIn() {
        graph.traktAuthManager.setOnLaunchWebView {
            profile.stubProfileSyncEndpoints()
            library.stubLibrarySyncEndpoints()
            auth.stubLoggedInUser()
            calendar.stubWeek()
        }
    }

    /**
     * Registers OAuth WebView callback so that next `clickSignInButton()` lazily wires profile
     * endpoints and flips fake auth state to LOGGED_IN. Use when test cares only about user-card
     * surface and does not exercise library or UpNext sync.
     */
    fun stubProfileOnSignIn() {
        graph.traktAuthManager.setOnLaunchWebView {
            profile.stubProfileSyncEndpoints()
            auth.stubLoggedInUser()
        }
    }

    fun stubUnauthenticatedState() {
        discover.stubBrowseGraph()
        mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe, HttpStatusCode.Unauthorized)
    }

    /**
     * Wires endpoints needed for unauthenticated user journey. Show, season, episode, and
     * progress endpoints are already covered by path-pattern stubs registered in
     * [Discover.stubBrowseGraph] (called via [stubUnauthenticatedState]).
     */
    fun stubUnauthenticatedJourney() {
        stubUnauthenticatedState()
    }

    /**
     * Simulates 401-then-refresh round-trip on `/users/me`. Configures fake repo with fresh
     * AuthState so next refresh resolves successfully, and re-stubs profile endpoints so
     * post-refresh calls pass.
     */
    fun stubTokenRefresh(
        accessToken: String = "refreshed-access",
        refreshToken: String = "refreshed-refresh",
        tokenLifetimeSeconds: Long = 3600,
    ) {
        mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe, HttpStatusCode.Unauthorized)
        val refreshedAuthState = AuthState(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isAuthorized = true,
            expiresAt = Clock.System.now() + tokenLifetimeSeconds.seconds,
            tokenLifetimeSeconds = tokenLifetimeSeconds,
        )
        graph.traktAuthRepository.setRefreshOutcome(TokenRefreshResult.Success(refreshedAuthState))
        profile.stubProfileSyncEndpoints()
    }

    inner class Auth {
        fun stubLoggedInUser(
            accessToken: String = TEST_ACCESS_TOKEN,
            refreshToken: String = TEST_REFRESH_TOKEN,
            tokenLifetimeSeconds: Long = 3600,
        ) {
            val authState = AuthState(
                accessToken = accessToken,
                refreshToken = refreshToken,
                isAuthorized = true,
                expiresAt = Clock.System.now() + tokenLifetimeSeconds.seconds,
                tokenLifetimeSeconds = tokenLifetimeSeconds,
            )
            graph.traktAuthRepository.setAuthState(authState)
            graph.traktAuthRepository.setRefreshOutcome(TokenRefreshResult.Success(authState))
            runBlocking { graph.traktAuthRepository.setState(TraktAuthState.LOGGED_IN) }
        }
    }

    inner class Discover {
        /**
         * Stubs every endpoint reachable from the Discover surface: the Trakt and TMDB list
         * endpoints, plus the per-show graph (details, seasons, episodes, people, related,
         * videos, watched progress, TMDB show, credits, season, watch providers).
         */
        fun stubBrowseGraph() {
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowsFavoritedWeekly)
            mockHandler.stubEndpoint(Endpoints.Trakt.GenresShows)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowsTrending)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowsPopular)
            mockHandler.stubEndpoint(Endpoints.Tmdb.DiscoverTv)
            mockHandler.stubEndpoint(Endpoints.Trakt.SearchByTmdb)

            // Per-show Trakt endpoints — single canonical fixture for any trakt id.
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowDetails)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowSeasons)
            // Per-season episodes: catch-all returns empty so unstubbed seasons don't re-write
            // episode rows. The per-season stubs registered after win under last-registered-first-match
            // ordering. (Catalog has no entry for the catch-all because the empty-array fixture is
            // shared across endpoints and isn't tied to one resource folder.)
            mockHandler.stubPatternFixture(pathRegex = "/shows/\\d+/seasons/\\d+", fixturePath = "empty_array.json")
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowSeasonEpisodesS1)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowSeasonEpisodesS2)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowPeople)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowRelated)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowVideos)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowProgressWatched)

            // Per-show TMDB endpoints — single canonical fixture for any tmdb id.
            mockHandler.stubEndpoint(Endpoints.Tmdb.ShowDetails)
            mockHandler.stubEndpoint(Endpoints.Tmdb.Credits)
            mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetails)
            mockHandler.stubEndpoint(Endpoints.Tmdb.WatchProviders)
        }

        fun stubDiscoverError() {
            mockHandler.stubEndpoint(Endpoints.Trakt.GenresShows, HttpStatusCode.InternalServerError)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowsTrending, HttpStatusCode.InternalServerError)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowsPopular, HttpStatusCode.InternalServerError)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowsFavoritedWeekly, HttpStatusCode.InternalServerError)
            mockHandler.stub(path = "/shows/anticipated", body = "", status = HttpStatusCode.InternalServerError)
            mockHandler.stubEndpoint(Endpoints.Tmdb.DiscoverTv, HttpStatusCode.InternalServerError)
        }
    }

    inner class Profile {
        fun stubProfileSyncEndpoints(slug: String = TEST_PROFILE_SLUG) {
            mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe)
            mockHandler.stubEndpoint(Endpoints.Trakt.userStats(slug))
            mockHandler.stubEndpoint(Endpoints.Trakt.userLists(slug))
        }
    }

    inner class Library {
        fun stubLibrarySyncEndpoints() {
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncLastActivities)
            mockHandler.stubEndpoint(Endpoints.Trakt.UsersMeWatchlistShows)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowDetails)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowSeasons)
            mockHandler.stubEndpoint(Endpoints.Tmdb.ShowDetails)
            mockHandler.stubEndpoint(Endpoints.Tmdb.WatchProviders)

            val watchlist = FixtureLoader.load(Endpoints.Trakt.UsersMeWatchlistShows.successFixture)
            showFixtures(watchlist).forEach { mockHandler.stubShow(it) }
        }
    }

    inner class Search {
        fun stubSearch(query: String) {
            mockHandler.stubSearchByQuery(query)
        }

        fun stubEmptySearch() {
            mockHandler.stubFixture(path = Endpoints.Trakt.Search.path, fixturePath = EMPTY_ARRAY_FIXTURE)
        }

        fun stubSearchError(query: String) {
            mockHandler.stubSearchByQuery(query, HttpStatusCode.Forbidden)
        }
    }

    inner class UpNext {
        /**
         * Wires the post-pilot-watched flow:
         * - Accepts the `POST /sync/history` upload so `markEpisodeAsWatched` proceeds to refresh
         *   UpNext instead of throwing on the unstubbed upload.
         * - Overrides the per-show watched-progress endpoint with a response where pilot is the
         *   last watched episode and episode 2 is queued up. The exact path beats the pattern
         *   fixture registered in [Discover.stubBrowseGraph] because `MockEngineHandler`
         *   matches stubs in last-registered-wins order.
         */
        fun stubProgressAfterPilotWatched(showTraktId: Long) {
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncHistory, method = HttpMethod.Post)
            mockHandler.stubEndpoint(Endpoints.Trakt.showProgressAfterPilotWatched(showTraktId))
        }
    }

    inner class Calendar {
        fun stubWeek(weekStart: String = TEST_TODAY, days: Int = 7) {
            mockHandler.stubEndpoint(Endpoints.Trakt.calendar(weekStart, days))
        }

        fun stubEmptyWeek(weekStart: String = TEST_TODAY, days: Int = 7) {
            mockHandler.stubFixture(
                path = Endpoints.Trakt.calendar(weekStart, days).path,
                fixturePath = EMPTY_ARRAY_FIXTURE,
            )
        }

        fun stubWeekError(
            weekStart: String = TEST_TODAY,
            days: Int = 7,
            status: Int = 404,
        ) {
            mockHandler.stubEndpoint(Endpoints.Trakt.calendar(weekStart, days), HttpStatusCode.fromValue(status))
        }
    }

    inner class TraktLists {
        fun stubAddShowToList(listId: Long, slug: String = TEST_PROFILE_SLUG) {
            mockHandler.stubEndpoint(Endpoints.Trakt.addShowToList(slug, listId), method = HttpMethod.Post)
        }

        fun stubCreateList(slug: String = TEST_PROFILE_SLUG) {
            mockHandler.stubEndpoint(Endpoints.Trakt.createList(slug), method = HttpMethod.Post)
        }
    }
}
