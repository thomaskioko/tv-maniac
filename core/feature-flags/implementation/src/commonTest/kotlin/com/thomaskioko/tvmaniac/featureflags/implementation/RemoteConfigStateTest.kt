package com.thomaskioko.tvmaniac.featureflags.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RemoteConfigStateTest {

    @Test
    fun `should emit provided default given no update has occurred`() = runTest {
        val state = RemoteConfigState()

        state.observeBoolean("simkl_login_enabled", default = false).test {
            awaitItem() shouldBe false
        }
    }

    @Test
    fun `should emit new value after update`() = runTest {
        val state = RemoteConfigState()

        state.observeBoolean("simkl_login_enabled", default = false).test {
            awaitItem() shouldBe false

            state.update(mapOf("simkl_login_enabled" to true))

            awaitItem() shouldBe true
        }
    }

    @Test
    fun `should not emit duplicate values given same value written`() = runTest {
        val state = RemoteConfigState()

        state.observeBoolean("simkl_login_enabled", default = false).test {
            awaitItem() shouldBe false

            state.update(mapOf("simkl_login_enabled" to false))

            expectNoEvents()
        }
    }

    @Test
    fun `should emit Firebase source for any key`() = runTest {
        val state = RemoteConfigState()

        state.observeSource("simkl_login_enabled").test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            awaitComplete()
        }
    }

    @Test
    fun `should keep prior values when partial update applied`() = runTest {
        val state = RemoteConfigState()
        state.update(mapOf("first" to true, "second" to false))

        state.update(mapOf("first" to false))

        state.observeBoolean("first", default = true).test {
            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
        state.observeBoolean("second", default = true).test {
            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }
}
