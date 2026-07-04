package com.thomaskioko.tvmaniac.app.test.compose.stubs

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
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
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

internal const val TEST_ACCESS_TOKEN: String = "test-access"
internal const val TEST_REFRESH_TOKEN: String = "test-refresh"
internal const val TEST_PROFILE_SLUG: String = "integration-test-user"
internal const val TEST_TODAY: String = "2026-04-19"
internal const val TEST_SIMKL_USER_NAME: String = "simkl-test-user"
internal const val TEST_SIMKL_ACCOUNT_ID: Long = 12345678L

/** Trakt id of the list returned by `trakt/users/lists/create/success.json`. */
internal const val TEST_CREATED_LIST_TRAKT_ID: Long = 99887766L

/** Name of the list returned by `trakt/users/lists/create/success.json`. */
internal const val TEST_CREATED_LIST_NAME: String = "Watch Later"
internal const val TEST_NEXT_WEEK: String = "2026-04-26"
internal const val SIMKL_LOGIN_FLAG_KEY: String = "simkl_login_enabled"
internal const val ACCOUNT_SWITCH_FLAG_KEY: String = "enable_account_switch"

internal class Scenarios(
    private val mockHandler: MockEngineHandler,
    private val graph: TestAppComponent,
    private val rootRobot: RootRobot,
) {
    val auth: Auth = Auth()
    val discover: Discover = Discover()
    val profile: Profile = Profile()
    val simkl: Simkl = Simkl()
    val library: Library = Library()
    val watchlist: Watchlist = Watchlist()
    val search: Search = Search()
    val calendar: Calendar = Calendar()
    val upNext: UpNext = UpNext()
    val traktLists: TraktLists = TraktLists()
    val flags: Flags = Flags()
    val ratings: Ratings = Ratings()

    fun signInAndDismissRationale() {
        auth.stubLoggedInUser()
        profile.stubProfileSyncEndpoints()
        rootRobot.dismissNotificationRationale()
    }

    fun stubAuthenticatedSimklProfile() {
        simkl.stubLoggedInUser()
        simkl.stubProfileEndpoints()
        simkl.stubWatchedHistoryEndpoints()
        simkl.stubActivities()
    }

    fun stubAuthenticatedSimklStartWatching() {
        simkl.stubLoggedInUser()
        simkl.stubProfileEndpoints()
        simkl.stubPlanToWatchWatchlist()
        simkl.stubActivities()
    }

    fun stubAuthenticatedSync() {
        auth.stubLoggedInUser()
        discover.stubBrowseGraph()
        library.stubLibrarySyncEndpoints()
        watchlist.stubWatchlistSyncEndpoints()
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
        graph.oAuthLauncher.setOnLaunch {
            profile.stubProfileSyncEndpoints()
            library.stubLibrarySyncEndpoints()
            watchlist.stubWatchlistSyncEndpoints()
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
        graph.oAuthLauncher.setOnLaunch {
            profile.stubProfileSyncEndpoints()
            library.stubLibrarySyncEndpoints()
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
            runBlocking { graph.traktAuthRepository.setState(AccountAuthState.LOGGED_IN) }
            // Mirror the production sign-in path: a successful OAuth handshake
            // calls `saveTokens` which emits `loginEvents`. The fake's
            // `setState(LOGGED_IN)` only flips state, so we also emit the
            // login event here to drive ContinueWatchingTasksInitializer's collector.
            graph.traktAuthRepository.triggerLogin()
        }
    }

    inner class Simkl {
        fun stubLoggedInUser(
            accessToken: String = "simkl-test-access",
            refreshToken: String = "simkl-test-refresh",
            tokenLifetimeSeconds: Long = 3600,
        ) {
            runBlocking {
                graph.authStateHolder.saveTokens(
                    provider = AccountProvider.SIMKL,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresAtSeconds = (Clock.System.now() + tokenLifetimeSeconds.seconds).epochSeconds,
                )
            }
        }

        fun stubProfileEndpoints() {
            mockHandler.stubEndpoint(
                endpoint = Endpoints.Simkl.UsersSettings,
                method = HttpMethod.Post,
            )
            mockHandler.stubEndpoint(
                endpoint = Endpoints.Simkl.UsersStats,
                method = HttpMethod.Post,
            )
        }

        fun stubWatchedHistoryEndpoints() {
            mockHandler.stubEndpoint(Endpoints.Simkl.SyncAllItems)
        }

        fun stubPlanToWatchWatchlist() {
            mockHandler.stubFixture(
                path = Endpoints.Simkl.SyncAllItems.path,
                fixturePath = "simkl/sync/all-items/start_watching.json",
            )
        }

        fun stubActivities() {
            mockHandler.stubEndpoint(Endpoints.Simkl.SyncActivities)
        }

        fun stubCalendarFeed() {
            mockHandler.stubEndpoint(Endpoints.Simkl.CalendarTvFeed)
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
            // Per-season episodes: catch-all returns empty so unstubbed seasons don't re-write
            // episode rows. The per-season stubs registered after win under last-registered-first-match
            // ordering.
            mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetails)
            mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetailsS1)
            mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetailsS2)
            mockHandler.stubEndpoint(Endpoints.Tmdb.WatchProviders)
            mockHandler.stubPatternFixture(
                pathRegex = "/users/me/history/shows/\\d+",
                fixturePath = EMPTY_ARRAY_FIXTURE,
            )
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
            mockHandler.stubEndpoint(Endpoints.Trakt.UserListItems)
        }
    }

    inner class Library {
        fun stubLibrarySyncEndpoints() {
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncLastActivities)
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncWatchedShows)
            mockHandler.stubEndpoint(Endpoints.Trakt.UsersMeWatchlistShows)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowDetails)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowSeasons)
            mockHandler.stubEndpoint(Endpoints.Tmdb.ShowDetails)
            mockHandler.stubEndpoint(Endpoints.Tmdb.WatchProviders)

            val watchedShows = FixtureLoader.load(Endpoints.Trakt.SyncWatchedShows.successFixture)
            showFixtures(watchedShows).forEach { mockHandler.stubShow(it) }
        }
    }

    inner class Watchlist {
        /**
         * Stubs the Continue Watching pipeline that drives the Watchlist and Up Next tabs:
         * `/sync/progress/up_next_nitro` (default Nitro fetcher) plus the documented fallback
         * (`/sync/watched/shows` + `/sync/playback/episodes`). Per-show metadata fan-out reuses
         * [Library]'s show stubs since the same Trakt ids appear in both fixtures.
         */
        fun stubWatchlistSyncEndpoints() {
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncProgressUpNextNitro)
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncPlaybackEpisodes)
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncWatchedShows)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowProgressWatched)
            mockHandler.stubEndpoint(Endpoints.Trakt.UsersHiddenProgressWatched)

            val nitroShows = FixtureLoader.load(Endpoints.Trakt.SyncProgressUpNextNitro.successFixture)
            showFixtures(nitroShows).forEach { mockHandler.stubShow(it) }
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
         * Stubs `POST /sync/history` so the background launcher fired by `markEpisodeAsWatched`
         * resolves cleanly when pushing the local UPLOAD-pending row to Trakt. UpNext list and
         * count derive live from the local `watched_episodes` table, so no Trakt UpNext API stub
         * is required after the click.
         *
         * The unused [showTraktId] parameter is kept so callers can keep the per-show signature
         * if/when the upload assertion grows to verify a specific show id.
         */
        @Suppress("UNUSED_PARAMETER")
        fun stubProgressAfterPilotWatched(showTraktId: Long) {
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncHistory, method = HttpMethod.Post)
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

    /**
     * Stubs the Trakt rating endpoints so the pending-ratings drain (`RatingsSyncInitializer`)
     * and the show-details community-rating reconcile (`RatingsStore`) both resolve cleanly
     * instead of logging a background-sync failure to [com.thomaskioko.tvmaniac.syncstate.api.SyncObserver].
     */
    inner class Ratings {
        fun stubRatingsSync() {
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncRatingsAdd, method = HttpMethod.Post)
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncRatingsRemove, method = HttpMethod.Post)
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncRatingsShows)
            mockHandler.stubEndpoint(Endpoints.Trakt.ShowCommunityRating)
        }
    }

    inner class Flags {
        fun enableSimklLogin() {
            graph.featureFlagsRemoteConfig.setBoolean(SIMKL_LOGIN_FLAG_KEY, true)
        }

        fun enableAccountSwitch() {
            graph.featureFlagsRemoteConfig.setBoolean(ACCOUNT_SWITCH_FLAG_KEY, true)
        }
    }
}
