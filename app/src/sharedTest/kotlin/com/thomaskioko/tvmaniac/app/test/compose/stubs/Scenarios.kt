package com.thomaskioko.tvmaniac.app.test.compose.stubs

import androidx.compose.ui.test.ComposeUiTest
import androidx.datastore.preferences.core.edit
import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.app.test.TestAppComponent
import com.thomaskioko.tvmaniac.app.test.compose.robot.RootRobot
import com.thomaskioko.tvmaniac.datastore.implementation.DefaultDatastoreRepository
import com.thomaskioko.tvmaniac.testing.integration.HttpScenarios
import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import com.thomaskioko.tvmaniac.testing.integration.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.testing.integration.TEST_TODAY
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

internal const val TEST_ACCESS_TOKEN: String = "test-access"
internal const val TEST_REFRESH_TOKEN: String = "test-refresh"
internal const val TEST_SIMKL_USER_NAME: String = "simkl-test-user"
internal const val TEST_SIMKL_ACCOUNT_ID: Long = 12345678L
internal const val SIMKL_LOGIN_FLAG_KEY: String = "simkl_login_enabled"
internal const val ACCOUNT_SWITCH_FLAG_KEY: String = "enable_account_switch"
internal const val ENABLE_PAYWALL_FLAG_KEY: String = "enable_paywall"

/**
 * Android's view of the test backend. Every purely HTTP registration lives in [HttpScenarios] so
 * the JVM and iOS answer requests from the same saved responses; what stays here is the part
 * XCUITest could never reach anyway, namely mutating graph fakes and driving the UI.
 */
internal class Scenarios(
    mockHandler: MockEngineHandler,
    private val graph: TestAppComponent,
    private val rootRobot: RootRobot,
    private val composeUi: ComposeUiTest,
) {
    private val http: HttpScenarios = HttpScenarios(mockHandler)

    private fun runSetup(block: suspend () -> Unit) {
        var failure: Throwable? = null
        val job = CoroutineScope(Dispatchers.Main).launch(start = CoroutineStart.UNDISPATCHED) {
            runCatching { block() }.onFailure { failure = it }
        }
        if (!job.isCompleted) composeUi.waitForIdle()
        check(job.isCompleted) {
            "Scenario setup did not complete. It must not block the test thread, because every " +
                "dispatcher role resolves to the scheduler this thread advances."
        }
        failure?.let { throw it }
    }

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
    val settings: Settings = Settings()

    fun signInAndDismissRationale() {
        stubProfileEndpoints(SyncProviderSource.TRAKT)
        stubLoggedInUser(SyncProviderSource.TRAKT)
        rootRobot.dismissNotificationRationale()
    }

    /**
     * Provider-aware sign-in: flips the given account provider's fake auth state to LOGGED_IN,
     * dispatching to each provider's existing mechanism ([Auth.stubLoggedInUser] for Trakt via
     * `FakeTraktAuthRepository`, [Simkl.stubLoggedInUser] for Simkl via `AuthStateHolder`).
     */
    fun stubLoggedInUser(provider: SyncProviderSource) {
        when (provider) {
            SyncProviderSource.TRAKT -> auth.stubLoggedInUser()
            SyncProviderSource.SIMKL -> simkl.stubLoggedInUser()
        }
    }

    fun stubProfileEndpoints(provider: SyncProviderSource) {
        when (provider) {
            SyncProviderSource.TRAKT -> profile.stubProfileSyncEndpoints()
            SyncProviderSource.SIMKL -> simkl.stubProfileEndpoints()
        }
    }

    /**
     * Provider-aware watched-library sync: Trakt's watched-shows/activities set, or Simkl's
     * watched-history (`sync/all-items`).
     */
    fun stubLibrarySyncEndpoints(provider: SyncProviderSource) {
        when (provider) {
            SyncProviderSource.TRAKT -> library.stubLibrarySyncEndpoints()
            SyncProviderSource.SIMKL -> simkl.stubWatchedHistoryEndpoints()
        }
    }

    /**
     * Provider-aware watchlist / up-next sync: Trakt's Continue Watching pipeline, or Simkl's
     * plan-to-watch list.
     */
    fun stubWatchlistSyncEndpoints(provider: SyncProviderSource) {
        when (provider) {
            SyncProviderSource.TRAKT -> watchlist.stubWatchlistSyncEndpoints()
            SyncProviderSource.SIMKL -> simkl.stubPlanToWatchWatchlist()
        }
    }

    fun stubAuthenticatedSimklProfile() {
        stubProfileEndpoints(SyncProviderSource.SIMKL)
        stubLibrarySyncEndpoints(SyncProviderSource.SIMKL)
        simkl.stubActivities()
        stubLoggedInUser(SyncProviderSource.SIMKL)
    }

    fun stubAuthenticatedSimklStartWatching() {
        stubProfileEndpoints(SyncProviderSource.SIMKL)
        stubWatchlistSyncEndpoints(SyncProviderSource.SIMKL)
        simkl.stubActivities()
        stubLoggedInUser(SyncProviderSource.SIMKL)
    }

    fun stubAuthenticatedSync() {
        stubPublicCatalog()
        stubActiveProvider(SyncProviderSource.TRAKT)
    }

    /**
     * Authenticated Trakt session with the Simkl-login and account-switch feature flags enabled:
     * the precondition for exercising a Trakt-to-Simkl account switch.
     */
    fun stubAuthenticatedSyncWithAccountSwitch() {
        flags.enableSimklLogin()
        flags.enableAccountSwitch()
        stubAuthenticatedSync()
    }

    fun stubTmdb() {
        stubPublicCatalog()
    }

    fun stubPublicCatalog() {
        discover.stubBrowseGraph()
    }

    /**
     * Wires the given [SyncProviderSource]'s baseline authenticated session: sign-in state, plus
     * its catalog-declared endpoints. Host-aware matching in [MockEngineHandler] means this is
     * safe to call once per test alongside [stubPublicCatalog]; call sites don't need to know
     * which provider owns which path. A cross-provider account endpoint is added to the catalog
     * lists in `Endpoints`, not to a new branch here.
     *
     * Always pair this with [stubPublicCatalog]: it is the only source of TMDB/discover coverage
     * for either provider.
     */
    fun stubActiveProvider(
        provider: SyncProviderSource,
        overrides: () -> Unit = {},
    ) {
        when (provider) {
            SyncProviderSource.TRAKT -> {
                stubLoggedInUser(SyncProviderSource.TRAKT)
                http.stubTraktAuthenticatedEndpoints()
                overrides()
            }
            SyncProviderSource.SIMKL -> {
                flags.enableSimklLogin()
                http.stubSimklAuthenticatedEndpoints()
                // Overrides register after the catalog set because the last stub for a path wins,
                // and before sign-in because login-triggered sync must find them already there.
                overrides()
                stubLoggedInUser(SyncProviderSource.SIMKL)
            }
        }
    }

    fun stubUsersMeUnauthorized(provider: SyncProviderSource = SyncProviderSource.TRAKT) {
        when (provider) {
            SyncProviderSource.TRAKT -> http.stubTraktUsersMeUnauthorized()
            SyncProviderSource.SIMKL -> http.stubSimklUserSettingsUnauthorized()
        }
    }

    /**
     * Registers OAuth WebView callback so that next `clickSignInButton()` lazily wires the full
     * authenticated Trakt session and flips fake auth state to LOGGED_IN. Mirrors live OAuth
     * round-trip without pre-stubbing LOGGED_IN in `@Before`, so DefaultRootPresenter's
     * auth-state collector still observes real LOGGED_OUT to LOGGED_IN transition.
     */
    fun stubAuthenticatedSyncOnSignIn() {
        stubOnSignIn(SyncProviderSource.TRAKT)
    }

    /**
     * Registers OAuth WebView callback so that next `clickSignInButton()` lazily wires the
     * authenticated Trakt session and flips fake auth state to LOGGED_IN.
     */
    fun stubProfileOnSignIn() {
        stubOnSignIn(SyncProviderSource.TRAKT)
    }

    /**
     * Registers the OAuth callback so the next sign-in click lazily wires the given provider's
     * authenticated session, mirroring a live OAuth round-trip. Owns the only `graph.oAuthLauncher`
     * access so journey tests never reach into the launcher directly.
     */
    fun stubOnSignIn(provider: SyncProviderSource) {
        graph.oAuthLauncher.setOnLaunch {
            when (provider) {
                SyncProviderSource.TRAKT -> stubActiveProvider(SyncProviderSource.TRAKT)
                SyncProviderSource.SIMKL -> stubAuthenticatedSimklProfile()
            }
        }
    }

    fun stubUnauthenticatedState(provider: SyncProviderSource = SyncProviderSource.TRAKT) {
        stubPublicCatalog()
        stubUsersMeUnauthorized(provider)
    }

    /**
     * Wires endpoints needed for unauthenticated user journey. Show, season, episode, and
     * progress endpoints are already covered by path-pattern stubs registered in
     * [Discover.stubBrowseGraph] (called via [stubUnauthenticatedState]).
     */
    fun stubUnauthenticatedJourney() {
        stubUnauthenticatedState()
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
            runSetup { graph.traktAuthRepository.setState(AccountAuthState.LOGGED_IN) }
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
            runSetup {
                graph.authStateHolder.saveTokens(
                    provider = SyncProviderSource.SIMKL,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresAtSeconds = (Clock.System.now() + tokenLifetimeSeconds.seconds).epochSeconds,
                )
            }
        }

        fun stubProfileEndpoints(): Unit = http.stubSimklProfileEndpoints()

        fun stubWatchedHistoryEndpoints(): Unit = http.stubSimklWatchedHistory()

        fun stubPlanToWatchWatchlist(): Unit = http.stubSimklPlanToWatchWatchlist()

        fun stubActivities(): Unit = http.stubSimklActivities()
    }

    inner class Discover {
        fun stubBrowseGraph(): Unit = http.stubBrowseGraph()
    }

    inner class Search {
        fun stubSearch(query: String): Unit = http.stubSearch(query)

        fun stubEmptySearch(): Unit = http.stubEmptySearch()

        fun stubSearchError(query: String): Unit = http.stubSearchError(query)
    }

    inner class UpNext {
        fun stubProgressAfterPilotWatched(
            showTraktId: Long,
            provider: SyncProviderSource = SyncProviderSource.TRAKT,
        ) {
            when (provider) {
                SyncProviderSource.TRAKT -> http.stubTraktSyncHistoryPost()
                SyncProviderSource.SIMKL -> http.stubSimklSyncHistoryPost()
            }
        }
    }

    inner class Calendar {
        fun stubWeek(
            provider: SyncProviderSource = SyncProviderSource.TRAKT,
            weekStart: String = TEST_TODAY,
            days: Int = 7,
        ) {
            when (provider) {
                SyncProviderSource.TRAKT -> http.stubTraktCalendarWeek(weekStart, days)
                SyncProviderSource.SIMKL -> http.stubSimklCalendarFeed()
            }
        }

        fun stubEmptyWeek(
            provider: SyncProviderSource = SyncProviderSource.TRAKT,
            weekStart: String = TEST_TODAY,
            days: Int = 7,
        ) {
            when (provider) {
                SyncProviderSource.TRAKT -> http.stubTraktCalendarWeekEmpty(weekStart, days)
                SyncProviderSource.SIMKL -> http.stubSimklCalendarFeedEmpty()
            }
        }

        fun stubWeekError(
            provider: SyncProviderSource = SyncProviderSource.TRAKT,
            weekStart: String = TEST_TODAY,
            days: Int = 7,
            status: Int = 404,
        ) {
            val code = HttpStatusCode.fromValue(status)
            when (provider) {
                SyncProviderSource.TRAKT -> http.stubTraktCalendarWeekError(weekStart, days, code)
                SyncProviderSource.SIMKL -> http.stubSimklCalendarFeedError(code)
            }
        }
    }

    inner class Library {
        fun stubLibrarySyncEndpoints(): Unit = http.stubTraktLibrarySync()
    }

    inner class Watchlist {
        fun stubWatchlistSyncEndpoints(): Unit = http.stubTraktWatchlistSync()
    }

    inner class Profile {
        fun stubProfileSyncEndpoints(slug: String = TEST_PROFILE_SLUG): Unit = http.stubTraktProfileSync(slug)
    }

    inner class TraktLists {
        fun stubAddShowToList(listId: Long, slug: String = TEST_PROFILE_SLUG): Unit =
            http.stubTraktAddShowToList(listId, slug)

        fun stubCreateList(slug: String = TEST_PROFILE_SLUG): Unit = http.stubTraktCreateList(slug)
    }

    inner class Flags {
        fun enableSimklLogin() {
            graph.featureFlagsRemoteConfig.setBoolean(SIMKL_LOGIN_FLAG_KEY, true)
        }

        fun enableAccountSwitch() {
            graph.featureFlagsRemoteConfig.setBoolean(ACCOUNT_SWITCH_FLAG_KEY, true)
        }

        fun enablePaywall() {
            graph.featureFlagsRemoteConfig.setBoolean(ENABLE_PAYWALL_FLAG_KEY, true)
        }
    }

    inner class Settings {
        fun disableMultiplePlays() {
            runSetup {
                graph.dataStore.edit { preferences ->
                    preferences[DefaultDatastoreRepository.KEY_MULTIPLE_PLAYS_ENABLED] = false
                }
            }
        }
    }
}
