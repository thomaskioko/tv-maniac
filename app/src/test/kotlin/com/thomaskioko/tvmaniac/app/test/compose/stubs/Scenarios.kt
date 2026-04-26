package com.thomaskioko.tvmaniac.app.test.compose.stubs

import com.thomaskioko.tvmaniac.app.test.TestAppComponent
import com.thomaskioko.tvmaniac.testing.integration.ui.NetworkResponse
import com.thomaskioko.tvmaniac.testing.integration.ui.NetworkStub
import com.thomaskioko.tvmaniac.testing.integration.ui.stubFixture
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

internal const val TEST_ACCESS_TOKEN: String = "test-access"
internal const val TEST_REFRESH_TOKEN: String = "test-refresh"
internal const val TEST_PROFILE_SLUG: String = "integration-test-user"

internal class Scenarios(
    private val stubber: NetworkStub,
    private val graph: TestAppComponent,
) {
    val auth: Auth = Auth()
    val discover: Discover = Discover()
    val showDetails: ShowDetails = ShowDetails()
    val profile: Profile = Profile()
    val library: Library = Library()
    val search: Search = Search()

    fun stubDiscoverBrowse() {
        discover.stubDiscoverEndpoints()
    }

    fun stubShowDetailsBrowse(traktShowId: Long) {
        discover.stubDiscoverEndpoints()
        showDetails.stubShowDetailsEndpoints(traktShowId = traktShowId)
    }

    fun stubShowDetailsNavigation(
        traktShowId: Long,
        tmdbShowId: Long,
        seasonNumbers: List<Long>,
    ) {
        discover.stubDiscoverEndpoints()
        showDetails.stubShowDetailsEndpoints(traktShowId = traktShowId)
        seasonNumbers.forEach { seasonNumber ->
            showDetails.stubSeasonDetailsEndpoints(
                traktShowId = traktShowId,
                tmdbShowId = tmdbShowId,
                seasonNumber = seasonNumber,
            )
        }
    }

    fun stubAuthenticatedSync(
        traktShowId: Long,
        tmdbShowId: Long,
        seasonNumbers: List<Long>,
    ) {
        auth.stubLoggedInUser()
        discover.stubDiscoverEndpoints()
        library.stubLibrarySyncEndpoints(
            traktShowId = traktShowId,
            tmdbShowId = tmdbShowId,
        )
        library.stubUpNextSyncEndpoints(
            traktShowId = traktShowId,
            tmdbShowId = tmdbShowId,
            seasonNumbers = seasonNumbers,
        )
        profile.stubProfileSyncEndpoints()
    }

    fun stubUnauthenticatedState() {
        discover.stubDiscoverEndpoints()
        stubber.stub(path = "/users/me", response = NetworkResponse.Error(status = 401))
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

        fun logout() {
            runBlocking {
                graph.traktAuthRepository.logout()
            }
        }
    }

    inner class Discover {
        fun stubDiscoverEndpoints() {
            stubber.stubFixture(path = "/genres/shows", fixturePath = "trakt/genres_shows.json")
            stubber.stubFixture(path = "/shows/trending", fixturePath = "trakt/shows_trending.json")
            stubber.stubFixture(path = "/shows/popular", fixturePath = "trakt/shows_popular.json")
            stubber.stubFixture(path = "/shows/favorited/weekly", fixturePath = "trakt/shows_favorited_weekly.json")

            stubber.stubFixture(path = "/3/discover/tv", fixturePath = "tmdb/discover_tv_upcoming.json")
            stubber.stubFixture(path = "/3/tv/1396", fixturePath = "tmdb/tv_details_1396.json")
            stubber.stubFixture(path = "/3/tv/60059", fixturePath = "tmdb/tv_details_60059.json")
            stubber.stubFixture(path = "/3/tv/1399", fixturePath = "tmdb/tv_details_1399.json")
        }
    }

    inner class ShowDetails {
        fun stubShowDetailsEndpoints(traktShowId: Long) {
            stubber.stubFixture(
                path = "/shows/$traktShowId",
                fixturePath = "trakt/show_details_$traktShowId.json",
            )
            stubber.stubFixture(
                path = "/shows/$traktShowId/seasons",
                fixturePath = "trakt/show_seasons_$traktShowId.json",
            )
        }

        fun stubSeasonDetailsEndpoints(
            traktShowId: Long,
            tmdbShowId: Long,
            seasonNumber: Long,
        ) {
            stubber.stubFixture(
                path = "/shows/$traktShowId/seasons/$seasonNumber",
                fixturePath = "trakt/show_season_episodes_${traktShowId}_$seasonNumber.json",
            )
            stubber.stubFixture(
                path = "/3/tv/$tmdbShowId/season/$seasonNumber",
                fixturePath = "tmdb/tv_details_60059.json",
            )
        }
    }

    inner class Profile {
        fun stubProfileSyncEndpoints(slug: String = TEST_PROFILE_SLUG) {
            stubber.stubFixture(
                path = "/users/me",
                fixturePath = "trakt/users_me.json",
            )
            stubber.stubFixture(
                path = "/users/$slug/stats",
                fixturePath = "trakt/users_me_stats.json",
            )
            stubber.stubFixture(
                path = "/users/$slug/lists",
                fixturePath = "trakt/users_me_lists.json",
            )
        }
    }

    inner class Library {
        fun stubLibrarySyncEndpoints(
            traktShowId: Long,
            tmdbShowId: Long,
        ) {
            stubber.stubFixture(
                path = "/sync/last_activities",
                fixturePath = "trakt/sync_last_activities.json",
            )
            stubber.stubFixture(
                path = "/users/me/watchlist/shows",
                fixturePath = "trakt/users_me_watchlist_shows.json",
            )
            showDetails.stubShowDetailsEndpoints(traktShowId = traktShowId)
            stubber.stubFixture(
                path = "/3/tv/$tmdbShowId",
                fixturePath = "tmdb/tv_details_$tmdbShowId.json",
            )
            stubber.stubFixture(
                path = "/3/tv/$tmdbShowId/watch/providers",
                fixturePath = "tmdb/watch_providers_$tmdbShowId.json",
            )
        }

        fun stubUpNextSyncEndpoints(
            traktShowId: Long,
            tmdbShowId: Long,
            seasonNumbers: List<Long>,
        ) {
            stubber.stubFixture(
                path = "/shows/$traktShowId/progress/watched",
                fixturePath = "trakt/show_progress_watched_$traktShowId.json",
            )
            seasonNumbers.forEach { seasonNumber ->
                showDetails.stubSeasonDetailsEndpoints(
                    traktShowId = traktShowId,
                    tmdbShowId = tmdbShowId,
                    seasonNumber = seasonNumber,
                )
            }
        }
    }

    inner class Search {
        fun stubSearch(query: String, traktShowId: Long, tmdbShowId: Long) {
            stubber.stubByQuery(path = "/search") { params ->
                if (params["query"] == query) {
                    NetworkResponse.Fixture("trakt/search_results.json")
                } else {
                    null
                }
            }
            stubber.stubFixture(
                path = "/3/tv/$tmdbShowId",
                fixturePath = "tmdb/tv_details_$tmdbShowId.json",
            )
        }

        fun stubEmptySearch() {
            stubber.stub(path = "/search", response = NetworkResponse.Success("[]"))
        }

        fun stubSearchError(query: String) {
            stubber.stubByQuery(path = "/search") { params ->
                if (params["type"] == "show" && params["query"] == query && params["extended"] == "full") {
                    NetworkResponse.Error(status = 403, body = "{}")
                } else {
                    NetworkResponse.Success("[]")
                }
            }
        }
    }
}
