package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.children.ChildNavState
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.serialization.Serializable

internal class MultiStackNavStateTest {

    @Serializable
    private data object DiscoverTestRoot : NavRoot

    @Serializable
    private data object LibraryTestRoot : NavRoot

    @Test
    fun `should mark active tab top RESUMED and every other entry CREATED`() {
        val state = MultiStackNavState(
            activeRoot = LibraryTestRoot,
            tabStacks = mapOf(
                DiscoverTestRoot to listOf(
                    DiscoverTestRoot,
                    ShowDetailsRoute(ShowDetailsParam(7)),
                ),
                LibraryTestRoot to listOf(
                    LibraryTestRoot,
                    MoreShowsRoute(99),
                ),
            ),
        )

        val statuses = state.children.map { it.configuration to it.status }

        statuses shouldBe listOf(
            TabbedRoute(DiscoverTestRoot, DiscoverTestRoot) to ChildNavState.Status.CREATED,
            TabbedRoute(DiscoverTestRoot, ShowDetailsRoute(ShowDetailsParam(7))) to ChildNavState.Status.CREATED,
            TabbedRoute(LibraryTestRoot, LibraryTestRoot) to ChildNavState.Status.CREATED,
            TabbedRoute(LibraryTestRoot, MoreShowsRoute(99)) to ChildNavState.Status.RESUMED,
        )
    }

    @Test
    fun `should require activeRoot present in tabStacks`() {
        runCatching {
            MultiStackNavState(
                activeRoot = LibraryTestRoot,
                tabStacks = mapOf(DiscoverTestRoot to listOf(DiscoverTestRoot)),
            )
        }.exceptionOrNull()?.message shouldBe "activeRoot LibraryTestRoot must be present in tabStacks [DiscoverTestRoot]"
    }

    @Test
    fun `should require non-empty stack for every tab`() {
        runCatching {
            MultiStackNavState(
                activeRoot = DiscoverTestRoot,
                tabStacks = mapOf(
                    DiscoverTestRoot to emptyList(),
                ),
            )
        }.exceptionOrNull()?.message shouldBe "Tab stack for DiscoverTestRoot must contain at least its root entry"
    }
}
