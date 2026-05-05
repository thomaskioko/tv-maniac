package com.thomaskioko.tvmaniac.app.test.graph

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.debug.nav.DebugRoute
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetParam
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetRoute
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.genreshows.nav.GenreShowsRoute
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.library.nav.LibraryRoot
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import com.thomaskioko.tvmaniac.search.nav.SearchRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.settings.nav.SettingsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlin.test.Test

internal class NavigationRouteTest : BaseAppFlowTest() {

    @Test
    fun `should resolve a NavDestination for every NavRoute subtype`() = runAppFlowTest {
        // 1. Enumerate all routable subtypes (including overlay routes such as EpisodeSheetRoute)
        val routes: List<NavRoute> = listOf(
            HomeRoute,
            SettingsRoute,
            SearchRoute,
            DebugRoute,
            TrailersRoute(traktShowId = 1L),
            ShowDetailsRoute(param = ShowDetailsParam(id = 1L)),
            SeasonDetailsRoute(
                param = SeasonDetailsUiParam(showTraktId = 1L, seasonId = 1L, seasonNumber = 1L),
            ),
            GenreShowsRoute(id = 1L),
            MoreShowsRoute(categoryId = 1L),
            EpisodeSheetRoute(EpisodeSheetParam(episodeId = 1L, source = ScreenSource.DISCOVER)),
        )

        val destinations = activityGraph.navDestinations
        destinations.shouldNotBeEmpty()

        // 2. Verify each route has a corresponding destination binding in the graph
        routes.forEach { route ->
            destinations.firstOrNull { it.matches(route) }.shouldNotBeNull()
        }
    }

    @Test
    fun `should resolve a TabDestination for every NavRoot subtype`() = runAppFlowTest {
        // 1. Enumerate all tab roots
        val roots: List<NavRoot> = listOf(
            DiscoverRoot,
            ProgressRoot,
            LibraryRoot,
            ProfileRoot,
        )

        // 2. Create the home-scoped graph
        val homeGraph = activityGraph.homeScreenGraphFactory
            .createHomeGraph(componentContext)
        val tabDestinations = homeGraph.tabDestinations
        tabDestinations.shouldNotBeEmpty()

        // 3. Verify each tab root has a matching destination in the home graph
        roots.forEach { root ->
            tabDestinations.firstOrNull { it.matches(root) }.shouldNotBeNull()
        }
    }

    @Test
    fun `should expose a NavRouteBinding for every routable NavRoute including overlays`() = runAppFlowTest {
        // 1. Enumerate routable classes (for serialization verification)
        val routableTypes = listOf(
            HomeRoute::class,
            SettingsRoute::class,
            SearchRoute::class,
            DebugRoute::class,
            TrailersRoute::class,
            ShowDetailsRoute::class,
            SeasonDetailsRoute::class,
            GenreShowsRoute::class,
            MoreShowsRoute::class,
            EpisodeSheetRoute::class,
        )

        val bindings = activityGraph.navRouteBindings
        bindings.shouldNotBeEmpty()

        // 2. Verify each class has a registered serializer binding (required for Decompose state restoration)
        routableTypes.forEach { kClass ->
            bindings.firstOrNull { it.kClass == kClass }.shouldNotBeNull()
        }
    }

    @Test
    fun `should resolve every codegen-generated graph extension factory`() = runAppFlowTest {
        // Verify factory chain: TestAppComponent -> ActivityGraph -> HomeScreenGraph
        val homeGraph = activityGraph.homeScreenGraphFactory
            .createHomeGraph(componentContext)
        homeGraph.homePresenter.shouldNotBeNull()

        // Verify Discover Tab graph (nested scope)
        val discoverTabGraph = homeGraph.discoverShowsTabGraphFactory
            .createDiscoverShowsTabGraph(componentContext)
        discoverTabGraph.discoverShowsPresenter.shouldNotBeNull()

        // Verify Show Details graph (assisted factory)
        val showDetailsGraph = activityGraph.showDetailsScreenGraphFactory
            .createShowDetailsGraph(componentContext)
        showDetailsGraph.showDetailsFactory
            .create(ShowDetailsParam(id = 1L))
            .shouldNotBeNull()
    }
}
