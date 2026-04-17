package com.thomaskioko.tvmaniac.navigation.testing

import com.thomaskioko.tvmaniac.navigation.NavRoute
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test

internal class NavigatorTest {

    @Serializable
    private object AlphaRoute : NavRoute

    @Serializable
    private data class BetaRoute(val id: Long) : NavRoute

    @Test
    fun `should pass when awaited push matches emitted event`() = runTest {
        val navigator = TestNavigator()

        navigator.test {
            navigator.pushNew(AlphaRoute)
            awaitPushNew(AlphaRoute)
        }
    }

    @Test
    fun `should fail when awaited push does not match emitted route`() = runTest {
        val navigator = TestNavigator()

        shouldThrow<IllegalStateException> {
            navigator.test {
                navigator.pushNew(AlphaRoute)
                awaitPushNew(BetaRoute(42))
            }
        }
    }

    @Test
    fun `should fail when awaited variant does not match emitted event`() = runTest {
        val navigator = TestNavigator()

        shouldThrow<IllegalStateException> {
            navigator.test {
                navigator.pushNew(AlphaRoute)
                awaitPop()
            }
        }
    }

    @Test
    fun `should pass when sequence of events is asserted in order`() = runTest {
        val navigator = TestNavigator()

        navigator.test {
            navigator.pushNew(AlphaRoute)
            navigator.bringToFront(BetaRoute(7))
            navigator.pop()

            awaitPushNew(AlphaRoute)
            awaitBringToFront(BetaRoute(7))
            awaitPop()
        }
    }

    @Test
    fun `should return next event via awaitEvent when variant is unknown`() = runTest {
        val navigator = TestNavigator()

        navigator.test {
            navigator.popTo(2)

            awaitEvent() shouldBe NavEvent.PopTo(2)
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
