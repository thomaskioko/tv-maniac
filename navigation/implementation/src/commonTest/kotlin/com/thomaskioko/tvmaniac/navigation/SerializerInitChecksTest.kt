package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.library.nav.LibraryRoot
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.watchlist.nav.WatchlistRoot
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

internal class SerializerInitChecksTest {

    @Test
    fun `should throw given DefaultBaseRouteSerializer with no bindings`() {
        val error = shouldThrow<IllegalArgumentException> {
            DefaultBaseRouteSerializer(
                routeBindings = emptySet(),
                rootBindings = emptySet(),
                navRoots = emptySet(),
            )
        }
        error.message shouldContain "at least one NavRouteBinding or NavRootBinding"
    }

    @Test
    fun `should throw given NavRoot without a matching NavRootBinding`() {
        val routeBindings = setOf<NavRouteBinding<*>>(
            NavRouteBinding(ShowDetailsRoute::class, ShowDetailsRoute.serializer()),
        )
        val rootBindings = setOf<NavRootBinding<*>>(
            NavRootBinding(DiscoverRoot::class, DiscoverRoot.serializer()),
        )

        val error = shouldThrow<IllegalArgumentException> {
            DefaultBaseRouteSerializer(
                routeBindings = routeBindings,
                rootBindings = rootBindings,
                navRoots = setOf(DiscoverRoot, LibraryRoot, WatchlistRoot),
            )
        }
        error.message shouldContain "Missing NavRootBinding"
        error.message shouldContain "LibraryRoot"
    }

    @Test
    fun `should throw given DefaultNavRouteSerializer with no bindings`() {
        val error = shouldThrow<IllegalArgumentException> {
            DefaultNavRouteSerializer(bindings = emptySet())
        }
        error.message shouldContain "at least one NavRouteBinding"
    }

    @Test
    fun `should throw given DefaultNavRootSerializer with no bindings`() {
        val error = shouldThrow<IllegalArgumentException> {
            DefaultNavRootSerializer(bindings = emptySet())
        }
        error.message shouldContain "at least one NavRootBinding"
    }
}
