package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test

internal class MultiStackNavStateSerializerTest {

    @Serializable
    private data object DiscoverTestRoot : NavRoot

    @Serializable
    private data object LibraryTestRoot : NavRoot

    private val routeBindings = setOf<NavRouteBinding<*>>(
        NavRouteBinding(ShowDetailsRoute::class, ShowDetailsRoute.serializer()),
        NavRouteBinding(MoreShowsRoute::class, MoreShowsRoute.serializer()),
        NavRouteBinding(SeasonDetailsRoute::class, SeasonDetailsRoute.serializer()),
    )
    private val rootBindings = setOf<NavRootBinding<*>>(
        NavRootBinding(DiscoverTestRoot::class, DiscoverTestRoot.serializer()),
        NavRootBinding(LibraryTestRoot::class, LibraryTestRoot.serializer()),
    )
    private val navRoots = setOf<NavRoot>(DiscoverTestRoot, LibraryTestRoot)
    private val serializer: MultiStackNavStateSerializer = MultiStackNavStateSerializer(
        baseRouteSerializer = DefaultBaseRouteSerializer(routeBindings, rootBindings, navRoots).serializer,
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

    @Test
    fun `should round-trip state with deep stack on inactive tab`() {
        val original = MultiStackNavState(
            activeRoot = DiscoverTestRoot,
            tabStacks = mapOf(
                DiscoverTestRoot to listOf(DiscoverTestRoot),
                LibraryTestRoot to listOf(
                    LibraryTestRoot,
                    ShowDetailsRoute(ShowDetailsParam(1)),
                    MoreShowsRoute(2),
                    ShowDetailsRoute(ShowDetailsParam(3)),
                    MoreShowsRoute(4),
                    ShowDetailsRoute(ShowDetailsParam(5)),
                ),
            ),
        )

        val restored = json.decodeFromString(serializer, json.encodeToString(serializer, original))

        restored shouldBe original
    }

    @Test
    fun `should preserve active root and stacks across simulated process death`() {
        val before = MultiStackNavState(
            activeRoot = LibraryTestRoot,
            tabStacks = mapOf(
                DiscoverTestRoot to listOf(DiscoverTestRoot, ShowDetailsRoute(ShowDetailsParam(7))),
                LibraryTestRoot to listOf(LibraryTestRoot, MoreShowsRoute(99)),
            ),
        )

        val encoded = json.encodeToString(serializer, before)
        val restored = json.decodeFromString(serializer, encoded)
        val afterSwitch = restored.copy(activeRoot = DiscoverTestRoot)

        restored.activeRoot shouldBe LibraryTestRoot
        restored.tabStacks[DiscoverTestRoot] shouldBe before.tabStacks[DiscoverTestRoot]
        restored.tabStacks[LibraryTestRoot] shouldBe before.tabStacks[LibraryTestRoot]
        afterSwitch.activeRoot shouldBe DiscoverTestRoot
    }

    @Test
    fun `should throw given encoded payload references an unregistered route subtype`() {
        val limitedRouteBindings = setOf<NavRouteBinding<*>>(
            NavRouteBinding(ShowDetailsRoute::class, ShowDetailsRoute.serializer()),
        )
        val limitedSerializer = MultiStackNavStateSerializer(
            baseRouteSerializer = DefaultBaseRouteSerializer(
                routeBindings = limitedRouteBindings,
                rootBindings = rootBindings,
                navRoots = navRoots,
            ).serializer,
            navRootSerializer = DefaultNavRootSerializer(rootBindings).serializer,
        )

        val payload = json.encodeToString(
            serializer,
            MultiStackNavState(
                activeRoot = DiscoverTestRoot,
                tabStacks = mapOf(
                    DiscoverTestRoot to listOf(
                        DiscoverTestRoot,
                        SeasonDetailsRoute(
                            SeasonDetailsUiParam(
                                showTraktId = 1,
                                seasonId = 2,
                                seasonNumber = 3,
                                forceRefresh = false,
                            ),
                        ),
                    ),
                    LibraryTestRoot to listOf(LibraryTestRoot),
                ),
            ),
        )

        shouldThrowAny {
            json.decodeFromString(limitedSerializer, payload)
        }
    }

    @Test
    fun `should reject construction with empty stack for a tab`() {
        shouldThrow<IllegalArgumentException> {
            MultiStackNavState(
                activeRoot = DiscoverTestRoot,
                tabStacks = mapOf(
                    DiscoverTestRoot to listOf(DiscoverTestRoot),
                    LibraryTestRoot to emptyList(),
                ),
            )
        }
    }
}
