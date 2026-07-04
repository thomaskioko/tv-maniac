package com.thomaskioko.tvmaniac.app.test.compose.stubs

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.app.test.TestAppComponent
import com.thomaskioko.tvmaniac.app.test.compose.robot.RootRobot
import com.thomaskioko.tvmaniac.testing.integration.EMPTY_ARRAY_FIXTURE
import com.thomaskioko.tvmaniac.testing.integration.Endpoint
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
    val simkl: Simkl = Simkl()
    val search: Search = Search()
    val calendar: Calendar = Calendar()
    val upNext: UpNext = UpNext()
    val traktLists: TraktLists = TraktLists()
    val flags: Flags = Flags()
    val library: Library = Library()
    val watchlist: Watchlist = Watchlist()
    val profile: Profile = Profile()

    fun signInAndDismissRationale() {
        stubLoggedInUser(AccountProvider.TRAKT)
        stubProfileEndpoints(AccountProvider.TRAKT)
        rootRobot.dismissNotificationRationale()
    }

    /**
     * Provider-aware sign-in: flips the given account provider's fake auth state to LOGGED_IN,
     * dispatching to each provider's existing mechanism ([Auth.stubLoggedInUser] for Trakt via
     * `FakeTraktAuthRepository`, [Simkl.stubLoggedInUser] for Simkl via `AuthStateHolder`).
     */
    fun stubLoggedInUser(provider: AccountProvider) {
        when (provider) {
            AccountProvider.TRAKT -> auth.stubLoggedInUser()
            AccountProvider.SIMKL -> simkl.stubLoggedInUser()
        }
    }

    fun stubProfileEndpoints(provider: AccountProvider) {
        when (provider) {
            AccountProvider.TRAKT -> profile.stubProfileSyncEndpoints()
            AccountProvider.SIMKL -> simkl.stubProfileEndpoints()
        }
    }

    fun stubAuthenticatedSimklProfile() {
        stubLoggedInUser(AccountProvider.SIMKL)
        stubProfileEndpoints(AccountProvider.SIMKL)
        simkl.stubWatchedHistoryEndpoints()
        simkl.stubActivities()
    }

    fun stubAuthenticatedSimklStartWatching() {
        stubLoggedInUser(AccountProvider.SIMKL)
        stubProfileEndpoints(AccountProvider.SIMKL)
        simkl.stubPlanToWatchWatchlist()
        simkl.stubActivities()
    }

    fun stubAuthenticatedSync() {
        stubPublicCatalog()
        stubActiveProvider(AccountProvider.TRAKT)
    }

    fun stubTmdb() {
        stubPublicCatalog()
    }

    fun stubPublicCatalog() {
        discover.stubBrowseGraph()
    }

    /**
     * Wires the given [AccountProvider]'s baseline authenticated session: sign-in state, plus its
     * catalog-declared [Endpoints.Trakt.authenticatedEndpoints] / [Endpoints.Simkl.authenticatedEndpoints]
     * in one shared loop. Host-aware matching in [MockEngineHandler] means this is safe to call
     * once per test alongside [stubPublicCatalog]; call sites don't need to know which provider
     * owns which path. A cross-provider account endpoint is added to those catalog lists, not to
     * a new branch here.
     *
     * Always pair this with [stubPublicCatalog]: it is the only source of TMDB/discover coverage
     * for either provider.
     */
    fun stubActiveProvider(provider: AccountProvider) {
        when (provider) {
            AccountProvider.TRAKT -> {
                stubLoggedInUser(AccountProvider.TRAKT)
                val endpoints = Endpoints.Trakt.authenticatedEndpoints + listOf(
                    Endpoints.Trakt.userStats(TEST_PROFILE_SLUG),
                    Endpoints.Trakt.userLists(TEST_PROFILE_SLUG),
                    Endpoints.Trakt.calendar(TEST_TODAY, 7),
                    Endpoints.Trakt.calendar(TEST_NEXT_WEEK, 7),
                )
                endpoints.forEach { mockHandler.stubEndpoint(it) }

                // Shared empty-array fixture, not tied to one catalog entry, so it isn't a
                // plain Endpoint. Kept as explicit glue, mirroring the seasons catch-all in
                // Discover.stubBrowseGraph.
                mockHandler.stubPatternFixture(
                    pathRegex = "/users/me/history/shows/\\d+",
                    fixturePath = EMPTY_ARRAY_FIXTURE,
                )

                val watchedShows = FixtureLoader.load(Endpoints.Trakt.SyncWatchedShows.successFixture)
                showFixtures(watchedShows).forEach { mockHandler.stubShow(it) }
                val nitroShows = FixtureLoader.load(Endpoints.Trakt.SyncProgressUpNextNitro.successFixture)
                showFixtures(nitroShows).forEach { mockHandler.stubShow(it) }
            }
            AccountProvider.SIMKL -> {
                flags.enableSimklLogin()
                Endpoints.Simkl.authenticatedEndpoints.forEach { mockHandler.stubEndpoint(it) }
                // Sign in LAST (see TRAKT note) so login-triggered sync finds its stubs.
                stubLoggedInUser(AccountProvider.SIMKL)
            }
        }
    }

    /** Fake authentication endpoint used to detect a signed-in account, per provider. */
    private fun userEndpoint(provider: AccountProvider): Endpoint.Exact =
        when (provider) {
            AccountProvider.TRAKT -> Endpoints.Trakt.UsersMe
            AccountProvider.SIMKL -> Endpoints.Simkl.UsersSettings
        }

    fun stubUsersMeUnauthorized(provider: AccountProvider = AccountProvider.TRAKT) {
        mockHandler.stubEndpoint(userEndpoint(provider), HttpStatusCode.Unauthorized)
    }

    /**
     * Registers OAuth WebView callback so that next `clickSignInButton()` lazily wires the full
     * authenticated Trakt session and flips fake auth state to LOGGED_IN. Mirrors live OAuth
     * round-trip without pre-stubbing LOGGED_IN in `@Before`, so DefaultRootPresenter's
     * auth-state collector still observes real LOGGED_OUT to LOGGED_IN transition.
     */
    fun stubAuthenticatedSyncOnSignIn() {
        graph.oAuthLauncher.setOnLaunch {
            stubActiveProvider(AccountProvider.TRAKT)
        }
    }

    /**
     * Registers OAuth WebView callback so that next `clickSignInButton()` lazily wires the
     * authenticated Trakt session and flips fake auth state to LOGGED_IN.
     */
    fun stubProfileOnSignIn() {
        graph.oAuthLauncher.setOnLaunch {
            stubActiveProvider(AccountProvider.TRAKT)
        }
    }

    fun stubUnauthenticatedState(provider: AccountProvider = AccountProvider.TRAKT) {
        stubPublicCatalog()
        mockHandler.stubEndpoint(userEndpoint(provider), HttpStatusCode.Unauthorized)
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
     * AuthState so next refresh resolves successfully, and re-stubs the profile endpoints so
     * post-refresh calls pass. Stubs the profile endpoints directly rather than going through
     * [stubActiveProvider], which would also reset the auth/refresh state this test is verifying.
     */
    fun stubTokenRefresh(
        provider: AccountProvider = AccountProvider.TRAKT,
        accessToken: String = "refreshed-access",
        refreshToken: String = "refreshed-refresh",
        tokenLifetimeSeconds: Long = 3600,
    ) {
        when (provider) {
            AccountProvider.TRAKT -> {
                mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe, HttpStatusCode.Unauthorized)
                val refreshedAuthState = AuthState(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    isAuthorized = true,
                    expiresAt = Clock.System.now() + tokenLifetimeSeconds.seconds,
                    tokenLifetimeSeconds = tokenLifetimeSeconds,
                )
                graph.traktAuthRepository.setRefreshOutcome(TokenRefreshResult.Success(refreshedAuthState))
                mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe)
                mockHandler.stubEndpoint(Endpoints.Trakt.userStats(TEST_PROFILE_SLUG))
                mockHandler.stubEndpoint(Endpoints.Trakt.userLists(TEST_PROFILE_SLUG))
                mockHandler.stubEndpoint(Endpoints.Trakt.UserListItems)
            }
            AccountProvider.SIMKL -> error(
                "Simkl token refresh is not stubbed yet; add it when a Simkl refresh journey exists.",
            )
        }
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
    }

    inner class Discover {
        /**
         * Stubs the agnostic public data graph reachable from the Discover surface, independent
         * of login state: the Trakt and TMDB list endpoints, plus the per-show graph (details,
         * seasons, episodes, people, related, videos, TMDB show, credits, season, watch
         * providers). Account-specific endpoints (e.g. watched progress, watch history) are
         * stubbed by [stubActiveProvider], not here.
         */
        fun stubBrowseGraph() {
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
            // Per-season episodes: catch-all returns empty so unstubbed seasons don't re-write
            // episode rows. The per-season stubs registered after win under last-registered-first-match
            // ordering.
            mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetails)
            mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetailsS1)
            mockHandler.stubEndpoint(Endpoints.Tmdb.SeasonDetailsS2)
            mockHandler.stubEndpoint(Endpoints.Tmdb.WatchProviders)
        }
    }

    inner class Search {
        fun stubSearch(query: String) {
            mockHandler.stubSearchByQuery(query)
        }

        fun stubEmptySearch() {
            mockHandler.stubFixture(path = Endpoints.PublicCatalog.Search.path, fixturePath = EMPTY_ARRAY_FIXTURE)
        }

        fun stubSearchError(query: String) {
            mockHandler.stubSearchByQuery(query, HttpStatusCode.Forbidden)
        }
    }

    inner class UpNext {
        /**
         * Stubs the provider's watched-history upload endpoint (`POST /sync/history` on both
         * Trakt and Simkl) so the background launcher fired by `markEpisodeAsWatched` resolves
         * cleanly when pushing the local UPLOAD-pending row. UpNext list and count derive live
         * from the local `watched_episodes` table, so no provider UpNext API stub is required
         * after the click.
         *
         * The unused [showTraktId] parameter is kept so callers can keep the per-show signature
         * if/when the upload assertion grows to verify a specific show id.
         */
        @Suppress("UNUSED_PARAMETER")
        fun stubProgressAfterPilotWatched(
            showTraktId: Long,
            provider: AccountProvider = AccountProvider.TRAKT,
        ) {
            when (provider) {
                AccountProvider.TRAKT -> mockHandler.stubEndpoint(Endpoints.Trakt.SyncHistory, method = HttpMethod.Post)
                AccountProvider.SIMKL -> mockHandler.stubEndpoint(Endpoints.Simkl.SyncHistory, method = HttpMethod.Post)
            }
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

    inner class Library {
        fun stubLibrarySyncEndpoints() {
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncLastActivities)
            mockHandler.stubEndpoint(Endpoints.Trakt.SyncWatchedShows)
            mockHandler.stubEndpoint(Endpoints.Trakt.UsersMeWatchlistShows)
            mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowDetails)
            mockHandler.stubEndpoint(Endpoints.PublicCatalog.ShowSeasons)
            mockHandler.stubEndpoint(Endpoints.Tmdb.ShowDetails)
            mockHandler.stubEndpoint(Endpoints.Tmdb.WatchProviders)

            val watchedShows = FixtureLoader.load(Endpoints.Trakt.SyncWatchedShows.successFixture)
            showFixtures(watchedShows).forEach { mockHandler.stubShow(it) }
        }
    }

    inner class Watchlist {
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

    inner class Profile {
        fun stubProfileSyncEndpoints(slug: String = TEST_PROFILE_SLUG) {
            mockHandler.stubEndpoint(Endpoints.Trakt.UsersMe)
            mockHandler.stubEndpoint(Endpoints.Trakt.userStats(slug))
            mockHandler.stubEndpoint(Endpoints.Trakt.userLists(slug))
            mockHandler.stubEndpoint(Endpoints.Trakt.UserListItems)
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

    inner class Flags {
        fun enableSimklLogin() {
            graph.featureFlagsRemoteConfig.setBoolean(SIMKL_LOGIN_FLAG_KEY, true)
        }

        fun enableAccountSwitch() {
            graph.featureFlagsRemoteConfig.setBoolean(ACCOUNT_SWITCH_FLAG_KEY, true)
        }
    }
}
