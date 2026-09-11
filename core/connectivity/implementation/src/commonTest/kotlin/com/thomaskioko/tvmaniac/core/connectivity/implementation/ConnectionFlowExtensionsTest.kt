package com.thomaskioko.tvmaniac.core.connectivity.implementation

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class ConnectionFlowExtensionsTest {

    private val settle = RECONNECT_DEBOUNCE + 1.milliseconds

    @Test
    fun `should emit nothing given initial connected value`() = runTest(StandardTestDispatcher()) {
        val connection = MutableStateFlow(true)

        connection.observeReconnections().test {
            advanceTimeBy(settle)
            runCurrent()

            expectNoEvents()
        }
    }

    @Test
    fun `should emit once given offline then online`() = runTest(StandardTestDispatcher()) {
        val connection = MutableStateFlow(true)

        connection.observeReconnections().test {
            advanceTimeBy(settle)
            runCurrent()

            connection.value = false
            advanceTimeBy(settle)
            runCurrent()

            connection.value = true
            advanceTimeBy(settle)
            runCurrent()

            awaitItem()
            expectNoEvents()
        }
    }

    @Test
    fun `should emit once given two offline and online flickers inside the debounce window`() = runTest(StandardTestDispatcher()) {
        val connection = MutableStateFlow(true)

        connection.observeReconnections().test {
            advanceTimeBy(settle)
            runCurrent()

            connection.value = false
            advanceTimeBy(settle)
            runCurrent()

            connection.value = true
            advanceTimeBy(500.milliseconds)
            connection.value = false
            advanceTimeBy(500.milliseconds)
            connection.value = true
            advanceTimeBy(settle)
            runCurrent()

            awaitItem()
            expectNoEvents()
        }
    }

    @Test
    fun `should emit nothing given going offline`() = runTest(StandardTestDispatcher()) {
        val connection = MutableStateFlow(true)

        connection.observeReconnections().test {
            advanceTimeBy(settle)
            runCurrent()

            connection.value = false
            advanceTimeBy(settle)
            runCurrent()

            expectNoEvents()
        }
    }

    @Test
    fun `should emit once given app started offline then online`() = runTest(StandardTestDispatcher()) {
        val connection = MutableStateFlow(false)

        connection.observeReconnections().test {
            advanceTimeBy(settle)
            runCurrent()

            connection.value = true
            advanceTimeBy(settle)
            runCurrent()

            awaitItem()
            expectNoEvents()
        }
    }
}
