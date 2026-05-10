package com.thomaskioko.tvmaniac.core.base.extensions

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TestTimeSource

internal class FlowExtensionsTest {

    @Test
    fun `should emit true immediately given source becomes true`() = runTest {
        val timeSource = TestTimeSource()
        val source = MutableStateFlow(false)

        source.minTrueDuration(1500.milliseconds, timeSource).test {
            awaitItem() shouldBe false

            source.value = true

            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should delay false emission given source becomes false before minimum duration elapses`() = runTest {
        val timeSource = TestTimeSource()
        val source = MutableStateFlow(false)

        source.minTrueDuration(1500.milliseconds, timeSource).test {
            awaitItem() shouldBe false

            source.value = true
            awaitItem() shouldBe true

            timeSource += 500.milliseconds
            source.value = false
            expectNoEvents()

            advanceTimeBy(1000.milliseconds)
            timeSource += 1000.milliseconds
            runCurrent()

            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit false immediately given source held true longer than minimum duration`() = runTest {
        val timeSource = TestTimeSource()
        val source = MutableStateFlow(false)

        source.minTrueDuration(1500.milliseconds, timeSource).test {
            awaitItem() shouldBe false

            source.value = true
            awaitItem() shouldBe true

            timeSource += 2000.milliseconds
            source.value = false

            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should suppress intermediate values given source toggles repeatedly within minimum duration`() = runTest {
        val timeSource = TestTimeSource()
        val source = MutableStateFlow(false)

        source.minTrueDuration(1500.milliseconds, timeSource).test {
            awaitItem() shouldBe false

            source.value = true
            awaitItem() shouldBe true

            timeSource += 200.milliseconds
            source.value = false
            expectNoEvents()

            timeSource += 100.milliseconds
            source.value = true
            expectNoEvents()

            timeSource += 2000.milliseconds
            advanceTimeBy(2000.milliseconds)
            runCurrent()
            expectNoEvents()

            source.value = false
            awaitItem() shouldBe false

            cancelAndIgnoreRemainingEvents()
        }
    }
}
