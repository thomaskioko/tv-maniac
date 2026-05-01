package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class MultiStackNavStateSerializerTest {

    @Serializable
    private data object DiscoverTestRoot : NavRoot

    @Serializable
    private data object LibraryTestRoot : NavRoot

    private val routeBindings = setOf<NavRouteBinding<*>>(
        NavRouteBinding(ShowDetailsRoute::class, ShowDetailsRoute.serializer()),
        NavRouteBinding(MoreShowsRoute::class, MoreShowsRoute.serializer()),
    )
    private val rootBindings = setOf<NavRootBinding<*>>(
        NavRootBinding(DiscoverTestRoot::class, DiscoverTestRoot.serializer()),
        NavRootBinding(LibraryTestRoot::class, LibraryTestRoot.serializer()),
    )
    private val serializer: MultiStackNavStateSerializer = MultiStackNavStateSerializer(
        baseRouteSerializer = DefaultBaseRouteSerializer(routeBindings, rootBindings).serializer,
        navRootSerializer = DefaultNavRootSerializer(rootBindings).serializer,
    )

    private val json = Json { allowStructuredMapKeys = true }

    @Test
    fun `should round-trip multi-tab state with active root and per-tab stacks`() {
        val original = MultiStackNavState(
            activeRoot = LibraryTestRoot,
            tabStacks = mapOf(
                DiscoverTestRoot to listOf(
                    DiscoverTestRoot,
                    ShowDetailsRoute(ShowDetailsParam(7)),
                ),
                LibraryTestRoot to listOf(
                    LibraryTestRoot,
                    MoreShowsRoute(99),
                    ShowDetailsRoute(ShowDetailsParam(42)),
                ),
            ),
        )

        val encoded = json.encodeToString(serializer, original)
        val restored = json.decodeFromString(serializer, encoded)

        restored shouldBe original
    }

    @Test
    fun `should round-trip state with single tab and only the root entry`() {
        val original = MultiStackNavState(
            activeRoot = DiscoverTestRoot,
            tabStacks = mapOf(DiscoverTestRoot to listOf(DiscoverTestRoot)),
        )

        val encoded = json.encodeToString(serializer, original)
        val restored = json.decodeFromString(serializer, encoded)

        restored shouldBe original
    }
}
