package com.thomaskioko.tvmaniac.navigation

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultNavEventBusTest {

    @Test
    fun `should deliver emitted event to collectors`() = runTest {
        val bus = DefaultNavEventBus()

        bus.events.test {
            bus.emit(NavEvent.ShowFollowed)
            awaitItem() shouldBe NavEvent.ShowFollowed
        }
    }

    @Test
    fun `should drop event given no active collector`() = runTest {
        val bus = DefaultNavEventBus()

        bus.emit(NavEvent.ShowFollowed)

        bus.events.test {
            expectNoEvents()
        }
    }
}
