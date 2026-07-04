package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.arkivanov.essenty.statekeeper.consumeRequired
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.builtins.ListSerializer
import kotlin.test.Test

internal class DefaultNavRouteSerializerTest {

    private val bindings: Set<NavRouteBinding<*>> = setOf(
        NavRouteBinding(HomeRoute::class, HomeRoute.serializer()),
        NavRouteBinding(ShowDetailsRoute::class, ShowDetailsRoute.serializer()),
        NavRouteBinding(SeasonDetailsRoute::class, SeasonDetailsRoute.serializer()),
        NavRouteBinding(MoreShowsRoute::class, MoreShowsRoute.serializer()),
    )

    private val serializer = DefaultNavRouteSerializer(bindings).serializer

    @Test
    fun `should round trip object route`() {
        val restored = roundTrip(HomeRoute)

        restored shouldBe HomeRoute
    }

    @Test
    fun `should round trip route with primitive param`() {
        val route = MoreShowsRoute(categoryId = 42L)

        roundTrip(route) shouldBe route
    }

    @Test
    fun `should round trip route with nested serializable param`() {
        val route = ShowDetailsRoute(ShowDetailsParam(showId = 1L, forceRefresh = true))

        roundTrip(route) shouldBe route
    }

    @Test
    fun `should round trip route with multi field nested param`() {
        val route = SeasonDetailsRoute(
            param = SeasonDetailsUiParam(
                showId = 10,
                seasonId = 20,
                seasonNumber = 3,
                forceRefresh = true,
            ),
        )

        roundTrip(route) shouldBe route
    }

    @Test
    fun `should round trip stack containing mixed routes`() {
        val stack: List<NavRoute> = listOf(
            HomeRoute,
            ShowDetailsRoute(ShowDetailsParam(1)),
            MoreShowsRoute(7),
        )

        val container = SerializableContainer(
            value = stack,
            strategy = ListSerializer(serializer),
        )

        val restored = container.consumeRequired(ListSerializer(serializer))
        restored shouldBe stack
    }

    @Test
    fun `should register each provided binding exactly once`() {
        val serializer = DefaultNavRouteSerializer(bindings).serializer

        serializer.descriptor.serialName shouldBe "PolymorphicSerializer"
    }

    @Test
    fun `should preserve data class field values given ShowDetailsRoute round trip`() {
        val original = ShowDetailsRoute(ShowDetailsParam(showId = 123L, forceRefresh = true))

        val restored = roundTrip(original).shouldBeInstanceOf<ShowDetailsRoute>()

        restored.param.showId shouldBe 123L
        restored.param.forceRefresh shouldBe true
    }

    @Test
    fun `should preserve data class field values given SeasonDetailsRoute round trip`() {
        val original = SeasonDetailsRoute(
            param = SeasonDetailsUiParam(
                showId = 10,
                seasonId = 20,
                seasonNumber = 3,
                forceRefresh = true,
            ),
        )

        val restored = roundTrip(original).shouldBeInstanceOf<SeasonDetailsRoute>()

        restored.param.showId shouldBe 10
        restored.param.seasonId shouldBe 20
        restored.param.seasonNumber shouldBe 3
        restored.param.forceRefresh shouldBe true
    }

    @Test
    fun `should preserve id given MoreShowsRoute round trip`() {
        val restored = roundTrip(MoreShowsRoute(categoryId = 99L)).shouldBeInstanceOf<MoreShowsRoute>()

        restored.categoryId shouldBe 99L
    }

    @Test
    fun `should preserve default values given ShowDetailsParam round trip`() {
        val original = ShowDetailsRoute(ShowDetailsParam(showId = 5L))

        val restored = roundTrip(original).shouldBeInstanceOf<ShowDetailsRoute>()

        restored.param.showId shouldBe 5L
        restored.param.forceRefresh shouldBe false
    }

    private fun roundTrip(route: NavRoute): NavRoute {
        val container = SerializableContainer(
            value = route,
            strategy = serializer,
        )
        return container.consumeRequired(serializer)
    }
}
