package com.thomaskioko.tvmaniac.navigation.testing

import com.thomaskioko.tvmaniac.navigation.NavRoute
import com.thomaskioko.tvmaniac.navigation.navigateBackTo
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable

internal class NavigatorTest {

    @Serializable
    private object AlphaRoute : NavRoute

    @Serializable
    private data class BetaRoute(val id: Long) : NavRoute

    @Test
    fun `should pass when awaited push matches emitted event`() = runTest {
        val navigator = TestNavigator()

        navigator.test {
            navigator.navigateTo(AlphaRoute)
            awaitNavigateTo(AlphaRoute)
        }
    }

    @Test
    fun `should fail when awaited push does not match emitted route`() = runTest {
        val navigator = TestNavigator()

        shouldThrow<IllegalStateException> {
            navigator.test {
                navigator.navigateTo(AlphaRoute)
                awaitNavigateTo(BetaRoute(42))
            }
        }
    }

    @Test
    fun `should fail when awaited variant does not match emitted event`() = runTest {
        val navigator = TestNavigator()

        shouldThrow<IllegalStateException> {
            navigator.test {
                navigator.navigateTo(AlphaRoute)
                awaitNavigateBack()
            }
        }
    }

    @Test
    fun `should pass when sequence of events is asserted in order`() = runTest {
        val navigator = TestNavigator()

        navigator.test {
            navigator.navigateTo(AlphaRoute)
            navigator.bringToFront(BetaRoute(7))
            navigator.navigateBack()

            awaitNavigateTo(AlphaRoute)
            awaitBringToFront(BetaRoute(7))
            awaitNavigateBack()
        }
    }

    @Test
    fun `should return next event via awaitEvent when variant is unknown`() = runTest {
        val navigator = TestNavigator()

        navigator.test {
            navigator.navigateBackTo<AlphaRoute>(inclusive = true)

            awaitEvent() shouldBe NavEvent.NavigateBackTo(AlphaRoute::class, inclusive = true)
        }
    }

    @Test
    fun `should pass expectNoEvents when no navigation has occurred`() = runTest {
        val navigator = TestNavigator()

        navigator.test {
            expectNoEvents()
        }
    }
}
